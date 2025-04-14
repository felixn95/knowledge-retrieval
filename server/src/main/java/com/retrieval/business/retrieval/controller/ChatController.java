package com.retrieval.business.retrieval.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.retrieval.KnowledgeRetrievalErrorCode;
import com.retrieval.business.retrieval.entity.*;
import com.retrieval.business.retrieval.util.CustomModelProducer;
import com.retrieval.business.retrieval.util.DocumentChunkMapper;
import com.retrieval.business.retrieval.util.PromptTemplateProvider;

/**
 * Controller class orchestrating chat requests.
 *
 * @author FelixNeubauer
 */
@ApplicationScoped
public class ChatController {

	private static final int DEFAULT_LIMIT = 15;
	private static final DistanceMetric DEFAULT_METRIC = DistanceMetric.COSINE;

	private static final String DE_INVALID_QUERY_MESSAGE = "Bitte stelle nur Fragen mit Bezug zu den entsprechenden Versicherungsbedingungen.";
	private static final String EN_INVALID_QUERY_MESSAGE = "Please only ask questions related to the corresponding insurance conditions.";

	@Inject
	private VectorSearchService vectorSearchService;

	@Inject
	private DocumentChunkMapper documentChunkMapper;

	@Inject
	private ConversationSessionService conversationSessionService;

	@Inject
	private CustomModelProducer customModelProducer;

	@Inject
	private PromptTemplateProvider promptTemplateProvider;

	@Inject
	private DocumentListController documentListController;

	@Inject
	private ChatResponseBuilder chatResponseBuilder;

	@Inject
	private ChatContextPreparer chatContextPreparer;

	@Inject
	private DomainsValidator domainsValidator;

	/**
	 * Processes the incoming chat request
	 *
	 * @param request containing the user's query and context
	 * @return a {@code ChatResponseDTO}
	 *
	 */
	public ChatResponseDTO processChat(final ChatRequestDTO request) {

		validateRequest(request);

		final String currentQuery = extractLatestUserQuery(request.getMessages());
		if (currentQuery.isEmpty()) {
			throw new IllegalArgumentException("User query cannot be empty.");
		}

		final ConversationSession session = Objects.requireNonNull(
				conversationSessionService.getSession(request.getSessionId()),
				"Conversation session must not be null");

		// Retrieve the relevant documents for the scoped domain.
		final Map<String, String> documentsMap = getRelatedDocumentMap(request);

		final QueryLanguage language = request.getContext().getLanguage();

		// Prepare the query context used to optimize the vector search.
		final QueryContext queryContext = chatContextPreparer.prepareQueryContext(currentQuery, session, language);

		// If the optimized query is empty, create an invalid query response and persist the session.
		if (queryContext.getQueryForVectorSearch().isEmpty()) {
			final String answer = "";
			final ChatResponseDTO chatResponse = createInvalidQueryResponse(language);
			conversationSessionService.updateConversation(session, currentQuery, answer, chatResponse, queryContext);
			conversationSessionService.persistOrUpdateSession(session);
			return chatResponse;
		}

		// Perform vector search for similar document chunks.
		final List<DocumentChunkEntity> similarChunks = vectorSearchService.searchByQuery(
				queryContext.getQueryForVectorSearch(),
				documentsMap.keySet().stream().toList(),
				DEFAULT_LIMIT,
				DEFAULT_METRIC
		);

		final List<DocumentChunkDTO> similarChunksDTOs = documentChunkMapper.toDto(similarChunks);

		// Enrich document chunks with corresponding document labels.
		similarChunksDTOs.forEach(chunk -> chunk.setDocumentLabel(documentsMap.get(chunk.getDocumentName())));

		// Build the prompt using the retrieved document chunks and history summary.
		final String prompt = promptTemplateProvider.buildRagPrompt(
				currentQuery,
				similarChunksDTOs,
				queryContext.getHistorySummary(),
				language
		);

		// Generate an answer using the custom OpenAI chat model.
		final String answer = customModelProducer.produceAzureOpenAiChatModel().generate(prompt);

		final ChatResponseDTO chatResponse = chatResponseBuilder.buildChatResponse(answer, similarChunksDTOs, session);

		// Update conversation state and persist session.
		conversationSessionService.updateConversation(session, currentQuery, answer, chatResponse, queryContext);
		conversationSessionService.persistOrUpdateSession(session);

		return chatResponse;
	}

	/**
	 * Creates a response for invalid or out-of-domain queries.
	 *
	 * @param language the query language
	 * @return a {@code ChatResponseDTO} with an appropriate invalid query message
	 */
	private ChatResponseDTO createInvalidQueryResponse(final QueryLanguage language) {
		final ChatMessageText chatMessageText = new ChatMessageText();
		if (QueryLanguage.GERMAN.equals(language)) {
			chatMessageText.setValue(DE_INVALID_QUERY_MESSAGE);
		} else {
			chatMessageText.setValue(EN_INVALID_QUERY_MESSAGE);
		}

		final ChatThreadEntry chatThreadEntry = new ChatThreadEntry();
		chatThreadEntry.setText(List.of(chatMessageText));

		final ChatResponseDTO chatResponse = new ChatResponseDTO();
		chatResponse.setResponses(List.of(chatThreadEntry));
		return chatResponse;
	}

	/**
	 * Validates the incoming chat request.
	 *
	 * @param request the chat request DTO; must not be null and must contain messages
	 */
	private void validateRequest(final ChatRequestDTO request) {
		Objects.requireNonNull(request, "ChatRequest must not be null.");
		Objects.requireNonNull(request.getMessages(), "Messages list must not be null.");
		validateDomains(request);
	}

	/**
	 * Validates the domain information of the chat request.
	 *
	 * @param request the chat request DTO containing domain information
	 */
	private void validateDomains(final ChatRequestDTO request) {
		if (request.getDomainsOfInterest() == null) {
			throw new IllegalArgumentException(KnowledgeRetrievalErrorCode.MISSING_DOMAINS_OF_INTEREST);
		} else {
			// Validate that at least one domain is evaluable.
			domainsValidator.atLeastOneDomainEvaluable(request);
		}
	}

	/**
	 * Retrieves the related document mapping based on the provided request.
	 *
	 * @param request the chat request DTO
	 * @return a {@code Map} of document IDs and their corresponding labels
	 * @throws RuntimeException if there is an IO failure in document retrieval
	 */
	private Map<String, String> getRelatedDocumentMap(final ChatRequestDTO request) {
		try {
			return documentListController.getDocumentMap(request);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to retrieve document IDs.", e);
		}
	}

	/**
	 * Extracts the latest user query from the list of chat messages.
	 *
	 * @param messages the list of chat messages
	 * @return the latest user query as a String; returns an empty String if none found
	 */
	private String extractLatestUserQuery(final List<ChatMessage> messages) {
		return messages.stream()
				.filter(Objects::nonNull)
				.filter(message -> ChatRole.USER.equals(message.getRole()) && message.getContent() != null)
				.map(ChatMessage::getContent)
				.reduce((first, second) -> second)
				.orElse("");
	}
}
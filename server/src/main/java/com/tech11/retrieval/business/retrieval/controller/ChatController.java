package com.tech11.retrieval.business.retrieval.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.tech11.retrieval.KnowledgeRetrievalErrorCode;
import com.tech11.retrieval.business.retrieval.entity.*;
import com.tech11.retrieval.business.retrieval.util.CustomModelProducer;
import com.tech11.retrieval.business.retrieval.util.DocumentChunkMapper;
import com.tech11.retrieval.business.retrieval.util.PromptTemplateProvider;

/**
 * Controller responsible for processing chat requests.
 *
 * @author FelixNeubauer
 */
@ApplicationScoped
public class ChatController {

	private static final int DEFAULT_LIMIT = 10;
	private static final DistanceMetric DEFAULT_METRIC = DistanceMetric.COSINE;
	private static final String INVALID_QUERY_MESSAGE = "Bitte stelle nur Fragen mit Bezug zu den entsprechenden Versicherungsbedingungen.";

	@Inject
	VectorSearchService vectorSearchService;

	@Inject
	DocumentChunkMapper documentChunkMapper;

	@Inject
	ConversationSessionService conversationSessionService;

	@Inject
	CustomModelProducer customModelProducer;

	@Inject
	PromptTemplateProvider promptTemplateProvider;

	@Inject
	DocumentListController documentListController;

	@Inject
	ChatResponseBuilder chatResponseBuilder;

	@Inject
	ChatContextPreparer chatContextPreparer;

	@Inject
	DomainsValidator domainsValidator;

	/**
	 * Processes the incoming chat request by orchestrating query extraction, document search, prompt building, answer
	 * generation, session update, and response building.
	 *
	 * @param request
	 *     The chat request DTO.
	 * @return A ChatResponseDTO containing the assistant’s response.
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

		// Retrieve relevant documents for the scoped domain.
		final Map<String, String> documentsMap = getRelatedDocumentMap(request);

		final QueryLanguage language = request.getContext().getLanguage();

		// Optimize the vector search.
		final QueryContext queryContext = chatContextPreparer.prepareQueryContext(
				currentQuery, session, language);

		// filter invalid queries (empty optimized query)
		if (queryContext.getQueryForVectorSearch().isEmpty()) {
			final String answer = "";
			final ChatResponseDTO chatResponse = createInvalidQueryResponse();
			// also persist the invalid query for future analysis
			conversationSessionService.updateConversation(
					session, currentQuery, answer, chatResponse, queryContext);
			conversationSessionService.persistOrUpdateSession(session);
			return chatResponse;
		}

		// Search for similar document chunks in vector space.
		final List<DocumentChunkEntity> similarChunks = vectorSearchService.searchByQuery(
				queryContext.getQueryForVectorSearch(), documentsMap.keySet().stream().toList(), DEFAULT_LIMIT,
				DEFAULT_METRIC);

		final List<DocumentChunkDTO> similarChunksDTOs = documentChunkMapper.toDto(similarChunks);

		// enrich the chunks with documentLabel
		similarChunksDTOs.forEach(chunk -> chunk.setDocumentLabel(documentsMap.get(chunk.getDocumentName())));

		final String prompt = promptTemplateProvider.buildRagPrompt(
				currentQuery, similarChunksDTOs, queryContext.getHistorySummary(), language);

		final String answer = customModelProducer
				.produceAzureOpenAiChatModel()
				.generate(prompt);

		final ChatResponseDTO chatResponse = chatResponseBuilder.buildChatResponse(
				answer, similarChunksDTOs, session);

		conversationSessionService.updateConversation(
				session, currentQuery, answer, chatResponse, queryContext);
		conversationSessionService.persistOrUpdateSession(session);

		return chatResponse;
	}

	/**
	 * Creates a response for invalid or out-of-domain queries.
	 *
	 * @return A ChatResponseDTO with an appropriate message.
	 */
	private ChatResponseDTO createInvalidQueryResponse() {
		final ChatMessageText chatMessageText = new ChatMessageText();
		chatMessageText.setValue(INVALID_QUERY_MESSAGE);

		final ChatThreadEntry chatThreadEntry = new ChatThreadEntry();
		chatThreadEntry.setText(List.of(chatMessageText));

		final ChatResponseDTO chatResponse = new ChatResponseDTO();
		chatResponse.setResponses(List.of(chatThreadEntry));
		return chatResponse;
	}

	/**
	 * Validates the incoming chat request.
	 *
	 * @param request
	 *     The chat request DTO.
	 */
	private void validateRequest(final ChatRequestDTO request) {
		Objects.requireNonNull(request, "ChatRequest must not be null.");
		Objects.requireNonNull(request.getMessages(), "Messages list must not be null.");

		validateDomains(request);
	}

	private void validateDomains(final ChatRequestDTO request) {
		if (request.getDomainsOfInterest() == null) {
			throw new IllegalArgumentException(
					KnowledgeRetrievalErrorCode.MISSING_DOMAINS_OF_INTEREST);
		} else {
			// Pass the full request so that domainsValidator can retrieve and validate existing document IDs.
			domainsValidator.atLeastOneDomainEvaluable(request);
		}
	}

	/**
	 * Retrieves document IDs based on the provided request.
	 *
	 * @param request
	 *     The chat request DTO.
	 * @return A list of document IDs.
	 */
	private Map<String, String> getRelatedDocumentMap(final ChatRequestDTO request) {
		try {
			return documentListController.getDocumentMap(request);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to retrieve document IDs.", e);
		}
	}

	/**
	 * Extracts the latest user query from the provided list of messages.
	 *
	 * @param messages
	 *     The list of chat messages.
	 * @return The latest user query as a String.
	 */
	private String extractLatestUserQuery(final List<ChatMessage> messages) {
		return messages.stream()
				.filter(Objects::nonNull)
				.filter(m -> ChatRole.USER.equals(m.getRole()) && m.getContent() != null)
				.map(ChatMessage::getContent)
				.reduce((first, second) -> second)
				.orElse("");
	}
}
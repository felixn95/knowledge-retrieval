package com.tech11.retrieval.business.retrieval.controller;

import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tech11.retrieval.business.retrieval.entity.ConversationSession;
import com.tech11.retrieval.business.retrieval.entity.QueryContext;
import com.tech11.retrieval.business.retrieval.entity.QueryLanguage;
import com.tech11.retrieval.business.retrieval.util.CustomModelProducer;
import com.tech11.retrieval.business.retrieval.util.PromptTemplateProvider;

/**
 * Prepares the query context and manages conversation session updates.
 *
 * @author FelixNeubauer
 */
@ApplicationScoped
public class ChatContextPreparer {

	@Inject
	protected HistoryCompressorService historyCompressorService;

	@Inject
	protected PromptTemplateProvider promptTemplateProvider;

	@Inject
	protected CustomModelProducer customModelProducer;

	/**
	 * Prepares the query context by combining the original query with the conversation history.
	 *
	 * @param originalQuery
	 *     the original user query
	 * @param session
	 *     the conversation session
	 * @return a QueryContext object containing the history summary and the optimized query
	 */
	public QueryContext prepareQueryContext(final String originalQuery, final ConversationSession session,
			final QueryLanguage language) {
		Objects.requireNonNull(originalQuery, "Original query must not be null");
		Objects.requireNonNull(session, "Conversation session must not be null");

		final QueryContext queryContext = new QueryContext();
		final String history = session.getMessages() == null || session.getMessages().isEmpty()
				? ""
				: historyCompressorService.extractedHistory(session);

		// Generate the optimized query and history summary in one LLM call
		final String prompt = promptTemplateProvider.getUnifiedOptimizationPrompt(history, originalQuery, language);
		final String llmResponse = customModelProducer.produceAzureOpenAiChatModelJsonFormat()
				.generate(prompt);

		JsonNode llmResponseNode;
		try {
			llmResponseNode = new ObjectMapper().readTree(llmResponse);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		final String historySummary = llmResponseNode.get("historySummary").asText();
		final String optimizedQuery = llmResponseNode.get("optimizedQuery").asText();

		queryContext.setHistorySummary(historySummary);
		queryContext.setQueryForVectorSearch(optimizedQuery);

		return queryContext;
	}
}

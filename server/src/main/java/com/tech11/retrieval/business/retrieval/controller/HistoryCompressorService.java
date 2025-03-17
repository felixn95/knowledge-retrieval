package com.tech11.retrieval.business.retrieval.controller;

import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.tech11.retrieval.business.retrieval.entity.ConversationSession;
import com.tech11.retrieval.business.retrieval.entity.QueryLanguage;
import com.tech11.retrieval.business.retrieval.util.CustomModelProducer;
import com.tech11.retrieval.business.retrieval.util.PromptTemplateProvider;

@ApplicationScoped
public class HistoryCompressorService {

	@Inject
	CustomModelProducer azureOpenAiChatModel;

	@Inject
	PromptTemplateProvider promptTemplateProvider;

	/**
	 * Compresses the conversation history into a brief summary.
	 */
	public String compressHistory(ConversationSession session) {

		String history = extractedHistory(session);

		String compressPrompt = promptTemplateProvider.historySummaryPrompt(QueryLanguage.GERMAN, history);
		return azureOpenAiChatModel.produceAzureOpenAiChatModel().generate(compressPrompt);
	}

	/**
	 * Extracts the conversation history into a single string.
	 */
	public String extractedHistory(ConversationSession session) {

		return session.getMessages().stream()
				.map(msg -> msg.getRole() + ": " + msg.getContent())
				.collect(Collectors.joining("\n"));
	}

	/**
	 * Generates a refined search query based on the conversation history summary and the current query.
	 */
	public String generateOptimizedSimilarityQuery(String historySummary, String currentQuery) {
		String prompt = promptTemplateProvider.getOptimizedHistorySimilaritySearch(historySummary, currentQuery,
				QueryLanguage.GERMAN);
		return azureOpenAiChatModel.produceAzureOpenAiChatModel().generate(prompt);
	}

}

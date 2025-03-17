package com.tech11.retrieval.business.retrieval.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.tech11.retrieval.business.retrieval.entity.DocumentChunkDTO;
import com.tech11.retrieval.business.retrieval.entity.DocumentChunkMetadataDTO;
import com.tech11.retrieval.business.retrieval.entity.QueryLanguage;

/*
 * Provides prompt templates for the different retrieval scenarios.
 *
 * @author FelixNeubauer
 */
@ApplicationScoped
public class PromptTemplateProvider {

	// Configuration properties
	@ConfigProperty(name = "documents.base-path")
	String basePath;

	@Inject
	ResultChunksPreparer resultChunksPreparer;

	@PostConstruct
	public void init() {
		Path absoluteBasePath = Paths.get(basePath).toAbsolutePath().normalize();
		if (!Files.exists(absoluteBasePath)) {
			throw new RuntimeException("Base path not found: " + absoluteBasePath.toString());
		}
		this.basePath = absoluteBasePath.toString();
	}

	// Template Paths
	private static final String TEMPLATE_BASE_PATH = "prompt_templates/";
	private static final String EN_UNIFIED_PROMPT = "en_unified_query_prompt.txt";
	private static final String DE_UNIFIED_PROMPT = "de_unified_query_prompt.txt";
	private static final String EN_SIMILARITY_PROMPT = "en_vector_similarity_compressing_prompt.txt";
	private static final String DE_SIMILARITY_PROMPT = "de_vector_similarity_compressing_prompt.txt";
	private static final String EN_RAG_PROMPT = "en_rag_prompt_template.txt";
	private static final String DE_RAG_PROMPT = "de_rag_prompt_template.txt";

	// Default messages
	private static final String NO_HISTORY_EN = "No conversation history available.";
	private static final String NO_HISTORY_DE = "Erste Anfrage, keine Historie vorhanden.";

	/**
	 * Retrieves a unified optimization prompt with placeholders replaced.
	 */
	public String getUnifiedOptimizationPrompt(final String history, final String currentQuery,
			final QueryLanguage language) {
		String template = loadTemplate(EN_UNIFIED_PROMPT, DE_UNIFIED_PROMPT, language);
		return template
				.replace("{history}", getFormattedHistory(history, language))
				.replace("{currentQuery}", currentQuery);
	}

	/**
	 * Builds an optimized history similarity search prompt.
	 */
	public String getOptimizedHistorySimilaritySearch(final String historySummary, final String currentQuery,
			final QueryLanguage language) {
		String template = loadTemplate(EN_SIMILARITY_PROMPT, DE_SIMILARITY_PROMPT, language);
		return template
				.replace("{historySummary}", Objects.toString(historySummary, ""))
				.replace("{currentQuery}", currentQuery);
	}

	/**
	 * Creates a history summary prompt.
	 */
	public String historySummaryPrompt(final QueryLanguage language, final String history) {
		String template = loadTemplate(EN_SIMILARITY_PROMPT, DE_SIMILARITY_PROMPT, language);
		return template.replace("{history}", history);
	}

	/**
	 * Builds the RAG (Retrieval-Augmented Generation) prompt with placeholders replaced.
	 */
	public String buildRagPrompt(final String query, final List<DocumentChunkDTO> similarChunks,
			final String historySummary, final QueryLanguage language) {
		final String template = loadTemplate(EN_RAG_PROMPT, DE_RAG_PROMPT, language);

		final String historyText = Optional.ofNullable(historySummary)
				.filter(summary -> !summary.isBlank())
				.map(summary -> language == QueryLanguage.GERMAN ? "Historie der Konversation:\n" + summary
						: "Conversation History:\n" + summary)
				.orElse(getNoHistoryMessage(language));

		final String context = resultChunksPreparer.prepareResultChunksJson(similarChunks);

		return template
				.replace("{history}", historyText)
				.replace("{context}", context)
				.replace("{query}", query);
	}

	/**
	 * Loads the appropriate template based on the language.
	 */
	private String loadTemplate(final String englishTemplate, final String germanTemplate,
			final QueryLanguage language) {
		final String relativePath = TEMPLATE_BASE_PATH
				+ (language == QueryLanguage.GERMAN ? germanTemplate : englishTemplate);
		final Path fullPath = Paths.get(basePath, relativePath);
		if (!Files.exists(fullPath)) {
			throw new RuntimeException("File not found: " + fullPath);
		}
		return PromptResourceLoader.loadResource(fullPath.toString());
	}

	/**
	 * Returns the default message for no conversation history based on the language.
	 */
	private String getNoHistoryMessage(final QueryLanguage language) {
		return language == QueryLanguage.GERMAN ? NO_HISTORY_DE : NO_HISTORY_EN;
	}

	/**
	 * Formats the history by checking if it's blank or null and replaces with the default message.
	 */
	private String getFormattedHistory(final String history, final QueryLanguage language) {
		return history == null || history.isBlank() ? getNoHistoryMessage(language) : history;
	}
}

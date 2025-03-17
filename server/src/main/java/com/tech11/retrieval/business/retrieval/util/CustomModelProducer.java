package com.tech11.retrieval.business.retrieval.util;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.ConfigProvider;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;
import dev.langchain4j.model.chat.request.ResponseFormat;

/**
 * Produces a specific model with the required configuration (f.x. a chat model from azure open ai).
 *
 * @author felixneubauer
 */
@ApplicationScoped
public class CustomModelProducer {

	public AzureOpenAiChatModel produceAzureOpenAiChatModel() {
		final double temperature = 0.3; // shall be configurable later
		return AzureOpenAiChatModel.builder()
				.apiKey(getRequiredConfig("AZURE_OPENAI_API_KEY_GERMANY"))
				.endpoint(getRequiredConfig("AZURE_OPENAI_ENDPOINT_GERMANY"))
				.deploymentName(getRequiredConfig("AZURE_OPENAI_CHAT_DEPLOYMENT_NAME"))
				.temperature(temperature)
				.logRequestsAndResponses(true)
				.build();
	}

	public AzureOpenAiEmbeddingModel produceAzureOpenAiEmbeddingModel() {
		return AzureOpenAiEmbeddingModel.builder()
				.apiKey(getRequiredConfig("AZURE_OPENAI_API_KEY_EMBEDDING_POLAND"))
				.endpoint(getRequiredConfig("AZURE_OPENAI_ENDPOINT_POLAND"))
				.deploymentName(getRequiredConfig("AZURE_OPENAI_EMBEDDING_DEPLOYMENT_NAME"))
				.logRequestsAndResponses(true)
				.build();
	}

	public AzureOpenAiChatModel produceAzureOpenAiChatModelJsonFormat() {
		final double temperature = 0.5; // shall be configurable later
		return AzureOpenAiChatModel.builder()
				.apiKey(getRequiredConfig("AZURE_OPENAI_API_KEY_GERMANY"))
				.endpoint(getRequiredConfig("AZURE_OPENAI_ENDPOINT_GERMANY"))
				.deploymentName(getRequiredConfig("AZURE_OPENAI_CHAT_DEPLOYMENT_NAME"))
				.temperature(temperature)
				.strictJsonSchema(true) // Enable strict JSON schema adherence
				.responseFormat(ResponseFormat.JSON) // Structured outputs in JSON format
				.logRequestsAndResponses(true)
				.build();
	}

	private String getRequiredConfig(final String key) {
		return ConfigProvider.getConfig()
				.getOptionalValue(key, String.class)
				.orElseThrow(() -> new IllegalStateException("Missing configuration property: " + key));
	}
}
package com.tech11.retrieval.business.retrieval.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.output.Response;

import com.tech11.retrieval.business.retrieval.util.CustomModelProducer;

/**
 * Service for creating embeddings.
 */
@ApplicationScoped
public class EmbeddingService {

	@Inject
	CustomModelProducer modelProducer;

	public float[] createEmbeddingAzureOpenAI(final String text) {
		// LangChain4J returns a Response with an Embedding object
		final Response<Embedding> response = modelProducer.produceAzureOpenAiEmbeddingModel().embed(text);
		final Embedding embedding = response.content();

		// Convert List<Float> to float[]
		float[] vector = new float[embedding.vectorAsList().size()];
		for (int i = 0; i < embedding.vectorAsList().size(); i++) {
			vector[i] = embedding.vectorAsList().get(i);
		}
		return vector;
	}
}
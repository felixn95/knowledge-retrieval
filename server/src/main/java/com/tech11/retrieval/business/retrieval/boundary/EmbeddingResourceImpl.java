package com.tech11.retrieval.business.retrieval.boundary;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import com.tech11.retrieval.business.retrieval.controller.EmbeddingService;
import com.tech11.retrieval.business.retrieval.entity.EmbedRequestDTO;

/**
 * Resource endpoint for managing embeddings.
 *
 * @author FelixNeubauer
 */
@RequestScoped
public class EmbeddingResourceImpl implements EmbeddingResource {

	@Inject
	EmbeddingService embeddingService;

	@Override
	public float[] createEmbeddingAzureOpenAI(final EmbedRequestDTO request) {
		return embeddingService.createEmbeddingAzureOpenAI(request.getTextChunk());
	}
}

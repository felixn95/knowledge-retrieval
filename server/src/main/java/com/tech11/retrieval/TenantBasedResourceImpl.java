package com.tech11.retrieval;

import jakarta.enterprise.context.RequestScoped;

import lombok.RequiredArgsConstructor;

import com.tech11.retrieval.business.retrieval.boundary.*;

/**
 * @author FelixNeubauer
 */

@RequiredArgsConstructor
@RequestScoped
public class TenantBasedResourceImpl implements TenantBasedResource {

	private final EmbeddingResource embeddingResource;
	private final IngestingResource ingestingResource;
	private final QueryResource queryResource;
	private final VectorSearchResource vectorSearchResource;
	private final FeedbackResource feedbackResource;
	private final ConfigDataResource configDataResource;

	@Override
	public QueryResource query(String tenantId) {
		return queryResource;
	}

	@Override
	public VectorSearchResource vectors(String tenantId) {
		return vectorSearchResource;
	}

	@Override
	public EmbeddingResource embeddings(final String tenantId) {
		return embeddingResource;
	}

	@Override
	public IngestingResource ingesting(String tenantId) {
		return ingestingResource;
	}

	@Override
	public FeedbackResource feedback(String tenantId) {
		return feedbackResource;
	}

	@Override
	public ConfigDataResource configData(String tenantId) {
		return configDataResource;
	}
}

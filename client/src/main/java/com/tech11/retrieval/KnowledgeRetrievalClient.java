package com.tech11.retrieval;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

/**
 * Reusable client for the Knowledge Retrieval Services.
 */
public class KnowledgeRetrievalClient {

	private final Client client;
	private final WebTarget baseTarget;
	private final String tenantId;

	// Adjust to match your actual context path
	public static final String CONTEXT_PATH = "/knowledge-retrieval";

	public KnowledgeRetrievalClient(String baseUrl, String tenantId) {
		this.client = ClientBuilder.newClient();
		this.baseTarget = client.target(baseUrl).path(CONTEXT_PATH);
		this.tenantId = tenantId;
	}

	public WebTarget embeddingResource() {
		// Adjust path according to your resources
		return baseTarget.path("tenants")
				.path(tenantId)
				.path("embeddings");
	}

	public WebTarget queryResource() {
		return baseTarget.path("tenants")
				.path(tenantId)
				.path("query");
	}

	public WebTarget ingestingResource() {
		return baseTarget.path("tenants")
				.path(tenantId)
				.path("ingesting");
	}

	public WebTarget vectorSearchResource() {
		return baseTarget.path("tenants")
				.path(tenantId)
				.path("vectors");
	}

	public WebTarget feedbackResource() {
		return baseTarget.path("tenants")
				.path(tenantId)
				.path("feedback");
	}

	public WebTarget configDataResource() {
		return baseTarget.path("tenants")
				.path(tenantId)
				.path("configData");
	}

	public void close() {
		client.close();
	}
}
package com.tech11.retrieval;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.tech11.retrieval.business.retrieval.boundary.*;

/**
 * Tenant-based resource interface for knowledge retrieval module.
 *
 * @author FelixNeubauer
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("{tenantId}")
public interface TenantBasedResource {

	@Path("query")
	QueryResource query(@PathParam("tenantId") final String tenantId);

	@Path("vectors")
	VectorSearchResource vectors(@PathParam("tenantId") final String tenantId);

	@Path("embeddings")
	EmbeddingResource embeddings(@PathParam("tenantId") final String tenantId);

	@Path("ingesting")
	IngestingResource ingesting(@PathParam("tenantId") final String tenantId);

	@Path("feedback")
	FeedbackResource feedback(@PathParam("tenantId") final String tenantId);

	@Path("configdata")
	ConfigDataResource configData(@PathParam("tenantId") final String tenantId);
}

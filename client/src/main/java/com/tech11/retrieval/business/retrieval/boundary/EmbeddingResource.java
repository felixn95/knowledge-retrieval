package com.tech11.retrieval.business.retrieval.boundary;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import com.tech11.retrieval.business.retrieval.entity.EmbedRequestDTO;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EmbeddingResource {

	/**
	 * Creates an embedding using Azure OpenAI.
	 *
	 * @param request
	 *     DTO containing the text chunk to embed.
	 * @return Array of floats representing the embedding.
	 */
	@POST
	@Path("/create-embedding")
	float[] createEmbeddingAzureOpenAI(@Valid EmbedRequestDTO request);
}

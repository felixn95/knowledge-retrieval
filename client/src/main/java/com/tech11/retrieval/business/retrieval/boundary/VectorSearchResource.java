package com.tech11.retrieval.business.retrieval.boundary;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.tech11.retrieval.business.retrieval.entity.VectorSearchRequestDTO;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface VectorSearchResource {

	/**
	 * POST endpoint to perform vector search. Expects a JSON body matching VectorSearchRequestDTO.
	 *
	 * @param request
	 *     the search request
	 * @return Response containing search results
	 */
	@POST
	@Path("/vector-search")
	Response searchByQuery(@Valid VectorSearchRequestDTO request);

	/**
	 * GET endpoint to retrieve a document chunk by document name and global index.
	 *
	 * @param documentName
	 *     the document name
	 * @param globalIndex
	 *     the global index
	 * @return Response containing the requested document chunk
	 */
	@GET
	@Path("/vector-search/chunk")
	Response getChunk(@QueryParam("documentName") String documentName, @QueryParam("globalIndex") int globalIndex);
}

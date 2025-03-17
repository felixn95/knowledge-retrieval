package com.tech11.retrieval.business.retrieval.boundary;

import java.util.List;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.tech11.retrieval.business.retrieval.entity.DocumentChunkDTO;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface IngestingResource {

	/**
	 * Ingests a JSON array of DocumentChunk objects.
	 *
	 * @param documentChunks
	 *     List of DocumentChunkDTO.
	 * @return Response indicating success or failure.
	 */
	@POST
	@Path("/ingest")
	Response ingestChunks(List<DocumentChunkDTO> documentChunks);

	/**
	 * Ingests documents from a specified folder and optional filename.
	 *
	 * @param folder
	 *     Folder under "document_resources/".
	 * @param filename
	 *     Optional filename, if specified only that file is processed.
	 * @return Response indicating success or failure.
	 */
	@POST
	@Path("/ingest-from-resource")
	Response ingestFromFile(@QueryParam("folder") String folder, @QueryParam("filename") String filename);
}

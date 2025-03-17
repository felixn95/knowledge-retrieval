package com.tech11.retrieval.business.retrieval.boundary;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import com.tech11.retrieval.business.retrieval.controller.IngestingService;
import com.tech11.retrieval.business.retrieval.entity.DocumentChunkDTO;

@RequestScoped
public class IngestingResourceImpl implements IngestingResource {

	@Inject
	IngestingService ingestService;

	@Override
	public Response ingestChunks(final List<DocumentChunkDTO> documentChunks) {
		if (documentChunks != null) {
			documentChunks.forEach(ingestService::processDocumentChunk);
		}
		return Response.ok().build();
	}

	@Override
	public Response ingestFromFile(final String folder, final String filename) {
		if (folder == null || folder.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("You must provide a valid folder name.").build();
		}
		try {
			ingestService.ingestFromResource(folder, filename);
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(e.getMessage()).build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
		return Response.ok("Ingestion successful.").build();
	}
}

package com.tech11.retrieval.business.retrieval.boundary;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

import com.tech11.retrieval.business.retrieval.controller.VectorSearchService;
import com.tech11.retrieval.business.retrieval.entity.DocumentChunkEntity;
import com.tech11.retrieval.business.retrieval.entity.VectorSearchRequestDTO;

@RequestScoped
public class VectorSearchResourceImpl implements VectorSearchResource {

	@Inject
	VectorSearchService vectorSearchService;

	@Override
	public Response searchByQuery(@Valid VectorSearchRequestDTO request) {
		List<DocumentChunkEntity> results = vectorSearchService.searchByQuery(
				request.getQuery(),
				request.getDocumentNames(),
				request.getLimit(),
				request.getMetric());
		return Response.ok(results).build();
	}

	@Override
	public Response getChunk(String documentName, int globalIndex) {
		DocumentChunkEntity chunk = vectorSearchService.getChunkByDocumentNameAndGlobalIndex(documentName, globalIndex);
		return Response.ok(chunk).build();
	}
}

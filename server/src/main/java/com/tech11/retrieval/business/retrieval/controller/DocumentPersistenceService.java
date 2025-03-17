package com.tech11.retrieval.business.retrieval.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import com.tech11.retrieval.business.retrieval.entity.DocumentChunkDTO;
import com.tech11.retrieval.business.retrieval.entity.DocumentChunkEntity;
import com.tech11.retrieval.business.retrieval.repository.DocumentChunkRepository;

@ApplicationScoped
public class DocumentPersistenceService {

	@Inject
	DocumentChunkRepository repository;

	@Transactional(TxType.REQUIRES_NEW)
	public void persistDocumentChunk(DocumentChunkDTO docChunk, float[] embedding) {
		if (docChunk == null || embedding == null) {
			throw new IllegalArgumentException("DocumentChunkDTO and embedding must not be null");
		}

		// Build metadata JSON using JSON-P
		JsonObjectBuilder builder = Json.createObjectBuilder();

		// Handle documentName
		builder.add("documentName",
				docChunk.getDocumentName() != null ? docChunk.getDocumentName() : "");

		// Handle pageNumber (assuming getPageNumber() returns a String)
		int pageNumber = -1;
		if (docChunk.getMetadata() != null && docChunk.getMetadata().getPageNumber() != null) {
			try {
				pageNumber = Integer.parseInt(docChunk.getMetadata().getPageNumber());
			} catch (NumberFormatException e) {
				// Log or handle parsing error if needed
			}
		}
		builder.add("pageNumber", pageNumber);

		// Handle pageHeader
		builder.add("pageHeader",
				(docChunk.getMetadata() != null && docChunk.getMetadata().getPageHeader() != null)
						? docChunk.getMetadata().getPageHeader()
						: "");

		// Handle sourceHeading
		builder.add("sourceHeading",
				(docChunk.getMetadata() != null && docChunk.getMetadata().getSourceHeading() != null)
						? docChunk.getMetadata().getSourceHeading()
						: "");

		// Handle chunkIndex (assuming getChunkIndex() also returns a String)
		int chunkIndex = -1;
		if (docChunk.getMetadata() != null && docChunk.getMetadata().getChunkIndex() != null) {
			try {
				chunkIndex = Integer.parseInt(docChunk.getMetadata().getChunkIndex());
			} catch (NumberFormatException e) {
				// Log or handle parsing error if needed
			}
		}
		builder.add("chunkIndex", chunkIndex);

		// Handle globalIndex (if docChunk.getGlobalIndex() is a Long or Integer, .add(...) should work directly)
		if (docChunk.getGlobalIndex() != null) {
			builder.add("globalIndex", docChunk.getGlobalIndex());
		} else {
			builder.addNull("globalIndex");
		}

		// Build final metadata JSON
		JsonObject metadataJson = builder.build();

		// Create and populate the entity
		DocumentChunkEntity entity = new DocumentChunkEntity();
		entity.setContent(docChunk.getChunkContent() != null ? docChunk.getChunkContent() : "");
		entity.setDocumentName(docChunk.getDocumentName() != null ? docChunk.getDocumentName() : "");
		entity.setGlobalIndex(docChunk.getGlobalIndex());
		entity.setMetadata(metadataJson);
		entity.setEmbedding(embedding);

		// Persist the entity
		repository.persist(entity);
	}
}
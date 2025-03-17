package com.tech11.retrieval.business.retrieval.util;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;

import com.tech11.retrieval.business.retrieval.controller.ConfigDataService;
import com.tech11.retrieval.business.retrieval.entity.DocumentChunkDTO;
import com.tech11.retrieval.business.retrieval.entity.DocumentChunkMetadataDTO;

@ApplicationScoped
public class ResultChunksPreparer {

	@Inject
	ConfigDataService configDataService;

	/**
	 * Prepares a JSON representation of the provided document chunks.
	 *
	 * @param similarChunks
	 *     the list of document chunks
	 * @return a JSON string representing the chunks
	 */
	public String prepareResultChunksJson(final List<DocumentChunkDTO> similarChunks) {
		final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		Optional.ofNullable(similarChunks)
				.filter(chunks -> !chunks.isEmpty())
				.ifPresent(chunks -> chunks.forEach(chunk -> arrayBuilder.add(formatChunk(chunk))));
		return arrayBuilder.build().toString();
	}

	/**
	 * Formats a single document chunk as a JSON object.
	 *
	 * @param chunk
	 *     the document chunk
	 * @return a JSON object builder for the chunk
	 */
	protected JsonObjectBuilder formatChunk(final DocumentChunkDTO chunk) {
		final String pageHeader = Optional.ofNullable(chunk.getMetadata())
				.map(DocumentChunkMetadataDTO::getPageHeader)
				.orElse("");
		final String sourceHeading = Optional.ofNullable(chunk.getMetadata())
				.map(DocumentChunkMetadataDTO::getSourceHeading)
				.orElse("");
		final String documentName = Optional.ofNullable(chunk.getDocumentName()).orElse("");
		final String pageNumber = Optional.ofNullable(chunk.getMetadata())
				.map(DocumentChunkMetadataDTO::getPageNumber)
				.map(Object::toString)
				.orElse("");
		final String chunkContent = Optional.ofNullable(chunk.getChunkContent()).orElse("");
		final String documentLabel = Optional.ofNullable(chunk.getDocumentLabel()).orElse("");

		return Json.createObjectBuilder()
				.add("chunkContent", chunkContent)
				.add("pageNumber", pageNumber)
				.add("pageHeader", pageHeader)
				.add("sourceHeading", sourceHeading)
				.add("documentLabel", documentLabel);
	}

}
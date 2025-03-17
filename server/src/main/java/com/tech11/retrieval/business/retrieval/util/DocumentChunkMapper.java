package com.tech11.retrieval.business.retrieval.util;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import com.tech11.retrieval.business.retrieval.entity.DocumentChunkDTO;
import com.tech11.retrieval.business.retrieval.entity.DocumentChunkEntity;
import com.tech11.retrieval.business.retrieval.entity.DocumentChunkMetadataDTO;

@ApplicationScoped
public class DocumentChunkMapper {

	// JSON-B instance
	private static final Jsonb JSONB = JsonbBuilder.create();

	public DocumentChunkDTO toDto(DocumentChunkEntity entity) {
		if (entity == null) {
			return null;
		}

		DocumentChunkDTO dto = new DocumentChunkDTO();
		dto.setDocumentName(entity.getDocumentName());
		dto.setGlobalIndex(entity.getGlobalIndex());
		dto.setChunkContent(entity.getContent());

		// Deserialize metadata from JSON to DocumentChunkMetadataDTO if present
		if (entity.getMetadata() != null) {
			try {
				// If getMetadata() returns a JsonObject, call .toString() first
				// If it returns a raw String, use it directly.
				String metadataStr = entity.getMetadata().toString();
				DocumentChunkMetadataDTO metadataDTO = JSONB.fromJson(metadataStr, DocumentChunkMetadataDTO.class);
				dto.setMetadata(metadataDTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dto;
	}

	public List<DocumentChunkDTO> toDto(List<DocumentChunkEntity> entities) {
		return entities.stream()
				.map(this::toDto)
				.collect(Collectors.toList());
	}
}
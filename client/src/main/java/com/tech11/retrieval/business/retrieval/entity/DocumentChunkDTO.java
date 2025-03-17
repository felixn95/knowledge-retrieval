package com.tech11.retrieval.business.retrieval.entity;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author FelixNeubauer
 */
@JsonbNillable
@Getter
@Setter
public class DocumentChunkDTO {

	@JsonbProperty("documentName")
	private String documentName;

	@JsonbProperty("documentLabel")
	private String documentLabel;

	@JsonbProperty("globalIndex")
	private Integer globalIndex;

	@JsonbProperty("chunkContent")
	private String chunkContent;

	@JsonbProperty("metadata")
	private DocumentChunkMetadataDTO metadata;
}
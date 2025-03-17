package com.tech11.retrieval.business.retrieval.entity;

import jakarta.json.bind.annotation.JsonbProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentChunkMetadataDTO {

	@JsonbProperty("chunkIndex")
	private String chunkIndex;

	@JsonbProperty("globalIndex")
	private String globalIndex;

	@JsonbProperty("documentName")
	private String documentName;

	@JsonbProperty("pageNumber")
	private String pageNumber;

	@JsonbProperty("pageHeader")
	private String pageHeader;

	@JsonbProperty("sourceHeading")
	private String sourceHeading;

}

package com.tech11.retrieval.business.retrieval.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RelatedDocumentDTO {
	private String documentId;
	private String relatedDomainKey;
	private List<String> versions; // Empty list means applicable to all versions
}

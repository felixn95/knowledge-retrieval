package com.tech11.retrieval.business.retrieval.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContractDomainDTO {
	private String contractKey;
	private List<RelatedDocumentDTO> relatedDocuments;
}

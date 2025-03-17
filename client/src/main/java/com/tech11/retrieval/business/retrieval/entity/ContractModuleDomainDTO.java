package com.tech11.retrieval.business.retrieval.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContractModuleDomainDTO {
	private String moduleKey;
	private String relatedContractKey;
	private List<RelatedDocumentDTO> relatedDocuments;
}

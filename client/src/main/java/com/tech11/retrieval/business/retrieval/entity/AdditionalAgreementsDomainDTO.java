package com.tech11.retrieval.business.retrieval.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalAgreementsDomainDTO {
	private String additionalAgreementKey;
	private List<RelatedDocumentDTO> relatedDocuments;
}

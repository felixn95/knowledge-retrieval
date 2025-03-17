package com.tech11.retrieval.business.retrieval.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * This class is used to store the general domain information for a product, f.x. multi-sectorial documents
 *  or documents that are not related to a specific contract.
 */

@Setter
@Getter
public class GeneralDomainDTO {
	private String generalDomainKey;
	private List<RelatedDocumentDTO> relatedDocuments;
}

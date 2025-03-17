package com.tech11.retrieval.business.retrieval.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * This class is used to store the domains of interest for filtering for related documents.
 */

@Setter
@Getter
public class DomainsOfInterestDTO {
	private String productKey;
	private List<ContractDomainDTO> contracts;
	private List<ContractModuleDomainDTO> contractModules;
	private List<AdditionalAgreementsDomainDTO> additionalAgreements;
	private List<GeneralDomainDTO> generalDomains;
}

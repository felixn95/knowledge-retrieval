package com.tech11.retrieval.business.retrieval.entity;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Class stores enriched policy data to enrich context with individual properties from policy db.
 *
 * @author FelixNeubauer
 */

@Getter
@Setter
public class IndividualPolicyDataDTO {

	private Map<String, Object> policyKeyProperties;

	private List<ContractPropertiesDTO> contractsProperties;

	private List<ContractModulesPropertiesDTO> contractModulesProperties;

	private Map<String, Object> additionalAgreements;

}

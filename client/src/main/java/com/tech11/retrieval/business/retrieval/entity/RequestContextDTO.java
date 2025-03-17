package com.tech11.retrieval.business.retrieval.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Extendable request context for any additional context required.
 *
 * @author FelixNeubauer
 */
@Getter
@Setter
public class RequestContextDTO {

	private QueryLanguage language;

	// enriched individual entity data
	private IndividualPolicyDataDTO individualPolicyData;

	// Business key
	private String businessKey;

	// Specify whether the response should be streamed (not used currently)
	private boolean stream;

}

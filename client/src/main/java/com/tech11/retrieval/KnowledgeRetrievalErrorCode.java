package com.tech11.retrieval;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KnowledgeRetrievalErrorCode {

	/**
	 * At least one domain must be selected.
	 */
	public static final String NO_DOMAIN_SELECTED = "KR-0000001001";

	/**
	 * Error retrieving document IDs.
	 */
	public static final String DOCUMENT_RETRIEVAL_FAILURE = "KR-0000001002";

	/**
	 * Domains of interest are missing.
	 */
	public static final String MISSING_DOMAINS_OF_INTEREST = "KR-0000001003";
}
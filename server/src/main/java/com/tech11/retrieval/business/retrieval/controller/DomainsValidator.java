package com.tech11.retrieval.business.retrieval.controller;

import java.io.IOException;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.tech11.retrieval.KnowledgeRetrievalErrorCode;
import com.tech11.retrieval.business.retrieval.entity.ChatRequestDTO;

/**
 * Validator for domains of interest.
 *
 * @author FelixNeubauer
 */
@ApplicationScoped
public class DomainsValidator {

	@Inject
	DocumentListController documentListController;

	protected void atLeastOneDomainEvaluable(final ChatRequestDTO request) {
		try {
			final List<String> evaluatedDocumentKeys = documentListController.getDocumentMap(request).keySet().stream()
					.toList();
			if (evaluatedDocumentKeys.isEmpty()) {
				throw new IllegalArgumentException(
						KnowledgeRetrievalErrorCode.NO_DOMAIN_SELECTED + ": At least one domain must be selected.");
			}
		} catch (IOException e) {
			throw new IllegalStateException(
					KnowledgeRetrievalErrorCode.DOCUMENT_RETRIEVAL_FAILURE + ": Error retrieving document IDs.", e);
		}
	}
}
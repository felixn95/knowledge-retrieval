package com.tech11.retrieval.business.retrieval.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.tech11.retrieval.business.retrieval.entity.ChatRequestDTO;
import com.tech11.retrieval.business.retrieval.entity.DomainsOfInterestDTO;

/**
 * Controller responsible for retrieving a map of related document IDs (keys) and document labels (values) based on the
 * domains provided in the request.
 */
@ApplicationScoped
public class DocumentListController {

	public static final String PRODUCT_DOCUMENTS_MAPPING_JSON = "/mapping-configs/product_documents_mapping.json";
	private final String documentsBasePath;
	private final ObjectMapper objectMapper;

	@Inject
	public DocumentListController(@ConfigProperty(name = "documents.base-path") final String documentsBasePath) {
		this.documentsBasePath = documentsBasePath;
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * Retrieves a map of document IDs → documentLabels based on the keys provided in the request.
	 *
	 * @param request
	 *     the chat request containing the domains of interest
	 * @return a map with document ID as key and document label as value
	 * @throws IOException
	 *     if there is an error reading the mapping file
	 * @throws IllegalArgumentException
	 *     if the mapping file is not found
	 */
	public Map<String, String> getDocumentMap(final ChatRequestDTO request) throws IOException {
		final DomainsOfInterestDTO reqDomains = request.getDomainsOfInterest();
		final String productKey = reqDomains.getProductKey();
		final String mappingFilePath = documentsBasePath + "/" + productKey + PRODUCT_DOCUMENTS_MAPPING_JSON;
		final File mappingFile = new File(mappingFilePath);

		if (!mappingFile.exists()) {
			throw new IllegalArgumentException("Mapping file not found for product: " + productKey);
		}

		// Load the JSON as a tree and locate the base node for domainsOfInterest.
		final JsonNode rootNode = objectMapper.readTree(mappingFile);
		final JsonNode domainsNode = getDomainsOfInterestNode(rootNode);

		// We will collect the doc IDs and labels in this map.
		final Map<String, String> documentMap = new HashMap<>();

		// Process contracts
		if (reqDomains.getContracts() != null) {
			reqDomains.getContracts().forEach(reqContract -> {
				final String searchKey = reqContract.getContractKey();
				extractDocuments(domainsNode, "contracts", "contractKey", searchKey, documentMap);
			});
		}

		// Process contract modules
		if (reqDomains.getContractModules() != null) {
			reqDomains.getContractModules().forEach(reqModule -> {
				final String searchKey = reqModule.getModuleKey();
				extractDocuments(domainsNode, "contractModules", "moduleKey", searchKey, documentMap);
			});
		}

		// Process additional agreements
		if (reqDomains.getAdditionalAgreements() != null) {
			reqDomains.getAdditionalAgreements().forEach(reqAgreement -> {
				final String searchKey = reqAgreement.getAdditionalAgreementKey();
				extractDocuments(domainsNode, "additionalAgreements", "additionalAgreementKey", searchKey, documentMap);
			});
		}

		// Process general domains
		if (reqDomains.getGeneralDomains() != null) {
			reqDomains.getGeneralDomains().forEach(reqGeneral -> {
				final String searchKey = reqGeneral.getGeneralDomainKey();
				extractDocuments(domainsNode, "generalDomains", "generalDomainKey", searchKey, documentMap);
			});
		}

		return documentMap;
	}

	/**
	 * Returns the node that contains the domainsOfInterest. If the root contains the key, that node is returned.
	 * Otherwise, the root itself is assumed to be the base.
	 *
	 * @param root
	 *     the root JsonNode from the mapping file
	 * @return the domainsOfInterest node
	 */
	private JsonNode getDomainsOfInterestNode(final JsonNode root) {
		return root.has("domainsOfInterest") ? root.get("domainsOfInterest") : root;
	}

	/**
	 * Searches for a matching domain object in the given category based on a key. For each matching domain, extracts
	 * the (documentId, documentLabel) pairs from "relatedDocuments" and adds them to the given map.
	 *
	 * @param domainsNode
	 *     the base JSON node for domains
	 * @param category
	 *     the domain category (e.g. "contracts", "contractModules")
	 * @param keyName
	 *     the name of the key to compare (e.g. "contractKey", "moduleKey")
	 * @param searchKey
	 *     the value to search for
	 * @param documentMap
	 *     the map to store documentId → documentLabel
	 */
	private void extractDocuments(final JsonNode domainsNode,
			final String category,
			final String keyName,
			final String searchKey,
			final Map<String, String> documentMap) {

		final JsonNode categoryArray = domainsNode.get(category);
		if (categoryArray != null && categoryArray.isArray()) {
			for (final JsonNode item : categoryArray) {
				final JsonNode keyNode = item.get(keyName);
				if (keyNode != null && keyNode.asText().equalsIgnoreCase(searchKey)) {
					final JsonNode relatedDocs = item.get("relatedDocuments");
					if (relatedDocs != null && relatedDocs.isArray()) {
						relatedDocs.forEach(doc -> {
							final JsonNode docIdNode = doc.get("documentId");
							final JsonNode docLabelNode = doc.get("documentLabel");
							if (docIdNode != null && docLabelNode != null) {
								documentMap.put(docIdNode.asText(), docLabelNode.asText());
							}
						});
					}
					// Assuming unique keys, break after finding the matching item.
					break;
				}
			}
		}
	}
}
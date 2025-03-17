package com.tech11.retrieval.business.retrieval.controller;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import com.tech11.retrieval.business.retrieval.entity.DistanceMetric;
import com.tech11.retrieval.business.retrieval.entity.DocumentChunkEntity;

/**
 * Service for performing vector-based search operations on document chunks. Provides methods to retrieve document
 * chunks based on similarity metrics and structured queries.
 *
 * @author FelixNeubauer
 */
@ApplicationScoped
public class VectorSearchService {

	@Inject
	EntityManager entityManager;

	@Inject
	EmbeddingService embeddingService;

	/**
	 * Searches for the best matching document chunks based on a query string. The query is embedded and then the chunks
	 * are filtered by document names and ordered by similarity, using the specified distance metric.
	 *
	 * @param query
	 *     the query string (e.g., a legal question)
	 * @param documentNames
	 *     list of document names to filter on
	 * @param limit
	 *     maximum number of results to return
	 * @param metric
	 *     the distance metric to use (e.g., COSINE, L2, etc.)
	 * @return List of matching DocumentChunkEntity
	 */
	@Transactional
	public List<DocumentChunkEntity> searchByQuery(final String query, final List<String> documentNames,
			final int limit, final DistanceMetric metric) {
		// Compute the query embedding.
		final float[] queryEmbedding = embeddingService.createEmbeddingAzureOpenAI(query);

		// Construct the JPQL query dynamically using the chosen distance metric.
		final String jpql = "FROM DocumentChunkEntity d " +
				"WHERE d.documentName IN :docNames " +
				"ORDER BY " + metric.getFunctionName() + "(d.embedding, :embedding)";
		final TypedQuery<DocumentChunkEntity> typedQuery = entityManager.createQuery(jpql, DocumentChunkEntity.class);
		typedQuery.setParameter("docNames", documentNames);
		typedQuery.setParameter("embedding", queryEmbedding);
		typedQuery.setMaxResults(limit);

		return typedQuery.getResultList();
	}

	/**
	 * Retrieves a specific document chunk based on its document name and global index. This method is useful for
	 * directly fetching a single chunk— for example, if you have a chunk with globalIndex 1 and want to retrieve the
	 * previous context at globalIndex 0.
	 *
	 * @param documentName
	 *     the document name
	 * @param globalIndex
	 *     the global index of the desired chunk
	 * @return the matching DocumentChunkEntity
	 * @throws IllegalArgumentException
	 *     if no matching chunk is found
	 */
	@Transactional
	public DocumentChunkEntity getChunkByDocumentNameAndGlobalIndex(final String documentName, final int globalIndex) {
		final String jpql = "FROM DocumentChunkEntity d WHERE d.documentName = :docName AND d.globalIndex = :globalIndex";
		final TypedQuery<DocumentChunkEntity> query = entityManager.createQuery(jpql, DocumentChunkEntity.class);
		query.setParameter("docName", documentName);
		query.setParameter("globalIndex", globalIndex);
		query.setMaxResults(1);
		try {
			return query.getSingleResult();
		} catch (final NoResultException e) {
			throw new IllegalArgumentException(
					"Chunk not found for document '" + documentName + "' and globalIndex " + globalIndex);
		}
	}
}
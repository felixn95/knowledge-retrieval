package com.retrieval.business.retrieval.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import com.retrieval.business.retrieval.entity.DocumentChunkEntity;

@ApplicationScoped
public class DocumentChunkRepository {

	@Inject
	EntityManager em;

	@Transactional
	public void persist(DocumentChunkEntity entity) {
		em.persist(entity);
	}
}

package com.tech11.retrieval.business.retrieval.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import com.tech11.retrieval.business.retrieval.entity.ConversationSessionEntity;

@ApplicationScoped
public class ConversationSessionRepository {

	@Inject
	EntityManager em;

	@Transactional
	public void persist(final ConversationSessionEntity entity) {
		em.persist(entity);
	}

	// findById method
	public ConversationSessionEntity findById(final String sessionId) {
		return em.find(ConversationSessionEntity.class, sessionId);
	}

	// persistOrUpdate method
	@Transactional
	public void persistOrUpdate(final ConversationSessionEntity entity) {
		em.merge(entity);
	}
}

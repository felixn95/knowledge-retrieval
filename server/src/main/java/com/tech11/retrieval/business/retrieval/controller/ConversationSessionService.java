package com.tech11.retrieval.business.retrieval.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.tech11.retrieval.business.retrieval.entity.*;
import com.tech11.retrieval.business.retrieval.repository.ConversationSessionRepository;

@ApplicationScoped
public class ConversationSessionService {

	@Inject
	ConversationSessionRepository sessionRepository;

	public void updateConversation(final ConversationSession session,
			final String query,
			final String answer,
			final ChatResponseDTO chatResponseDTO,
			final QueryContext queryContext) {
		Objects.requireNonNull(session, "Conversation session must not be null");

		initializeIfNull(session);

		final ChatMessage queryMessage = createChatMessage(query, ChatRole.USER);
		final ChatMessage answerMessage = createChatMessage(answer, ChatRole.ASSISTANT);

		addMessageIfAbsent(session, queryMessage, ChatRole.USER);
		addMessageIfAbsent(session, answerMessage, ChatRole.ASSISTANT);

		session.getMessages().add(queryMessage);
		session.getMessages().add(answerMessage);

		updateLastItem(session.getChatResponses(), chatResponseDTO);
		updateLastItem(session.getQueryContexts(), queryContext);
	}

	private void initializeIfNull(final ConversationSession session) {
		if (session.getMessages() == null)
			session.setMessages(new ArrayList<>());
		if (session.getChatResponses() == null)
			session.setChatResponses(new ArrayList<>());
		if (session.getQueryContexts() == null)
			session.setQueryContexts(new ArrayList<>());
	}

	private ChatMessage createChatMessage(final String content, final ChatRole role) {
		final ChatMessage message = new ChatMessage();
		message.setContent(content);
		message.setRole(role);
		return message;
	}

	private void addMessageIfAbsent(ConversationSession session, ChatMessage message, ChatRole role) {
		List<ChatMessage> messages = session.getMessages();
		if (messages.isEmpty() || !messages.getLast().getRole().equals(role)) {
			messages.add(message);
		}
	}

	private <T> void updateLastItem(List<T> list, T item) {
		if (list.isEmpty()) {
			list.add(item);
		} else {
			list.set(list.size() - 1, item);
		}
	}

	/**
	 * Retrieves a session by sessionId or creates a new one if not found.
	 */
	public ConversationSession getSession(final String sessionId) {
		if (sessionId != null) {
			final ConversationSessionEntity sessionEntity = sessionRepository.findById(sessionId);
			if (sessionEntity != null) {
				return sessionEntity.toDTO();
			}
		}

		// Create new session if none found
		final ConversationSessionEntity newSession = new ConversationSessionEntity();
		newSession.setSessionId(UUID.randomUUID().toString());
		sessionRepository.persist(newSession);
		return newSession.toDTO();
	}

	/**
	 * Updates the feedback for a session.
	 */
	public void updateFeedback(final ConversationSession session,
			final FeedbackDTO feedbackDTO) {
		if (session.getFeedbacks() == null) {
			session.setFeedbacks(new ArrayList<>());
		}
		session.getFeedbacks().add(feedbackDTO);
		persistOrUpdateSession(session);
	}

	/**
	 * Persists or updates a session.
	 */
	public void persistOrUpdateSession(final ConversationSession session) {
		// persistOrUpdate is provided by Panache.
		final ConversationSessionEntity sessionEntity = ConversationSessionEntity.fromDTO(session);
		sessionRepository.persistOrUpdate(sessionEntity);
	}
}

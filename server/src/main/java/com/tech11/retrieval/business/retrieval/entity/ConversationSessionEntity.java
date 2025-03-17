package com.tech11.retrieval.business.retrieval.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "conversation_session")
public class ConversationSessionEntity {

	@Id
	private String sessionId;

	@Column(columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<ChatMessage> messages = new ArrayList<>();

	// Store QueryContext entries as JSON array
	@Column(columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<QueryContext> queryContexts = new ArrayList<>();

	// Persist entire ChatResponseDTO as JSONB
	@Column(columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<ChatResponseDTO> chatResponses;

	// Store Feedback entries as JSON array
	@Column(columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<FeedbackDTO> feedbacks = new ArrayList<>();

	/**
	 * Converts a ConversationSession DTO to a ConversationSessionEntity. All fields are copied and null checks ensure
	 * no list remains null.
	 */
	public static ConversationSessionEntity fromDTO(ConversationSession dto) {
		if (dto == null) {
			return null;
		}
		final ConversationSessionEntity entity = new ConversationSessionEntity();
		// sessionId: use DTO's if present, else generate a new one.
		entity.setSessionId(dto.getSessionId() != null ? dto.getSessionId() : UUID.randomUUID().toString());

		// messages (transient)
		entity.setMessages(dto.getMessages() != null ? new ArrayList<>(dto.getMessages()) : new ArrayList<>());

		// chatResponses
		entity.setChatResponses(dto.getChatResponses());

		// queryContexts
		entity.setQueryContexts(dto.getQueryContexts() != null ? new ArrayList<>(dto.getQueryContexts())
				: new ArrayList<>());

		// feedbacks
		entity.setFeedbacks(dto.getFeedbacks() != null ? new ArrayList<>(dto.getFeedbacks())
				: new ArrayList<>());
		return entity;
	}

	/**
	 * Converts this ConversationSessionEntity to a ConversationSession DTO. All fields are copied and null checks
	 * ensure no list remains null.
	 */
	public ConversationSession toDTO() {
		final ConversationSession dto = new ConversationSession();
		// sessionId
		dto.setSessionId(this.getSessionId());

		// messages (transient)
		dto.setMessages(this.getMessages() != null ? new ArrayList<>(this.getMessages())
				: new ArrayList<>());

		// chatResponse
		dto.setChatResponses(this.getChatResponses());

		// queryContexts
		dto.setQueryContexts(this.getQueryContexts() != null ? new ArrayList<>(this.getQueryContexts())
				: new ArrayList<>());

		// feedbacks
		dto.setFeedbacks(this.getFeedbacks() != null ? new ArrayList<>(this.getFeedbacks())
				: new ArrayList<>());
		return dto;
	}
}

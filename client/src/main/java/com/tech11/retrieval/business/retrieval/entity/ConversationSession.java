package com.tech11.retrieval.business.retrieval.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationSession {
	private String sessionId;
	private List<ChatMessage> messages = new ArrayList<>();
	private List<ChatResponseDTO> chatResponses = new ArrayList<>();
	private List<QueryContext> queryContexts = new ArrayList<>();
	private List<FeedbackDTO> feedbacks = new ArrayList<>();

	public ConversationSession() {
		this.sessionId = UUID.randomUUID().toString();
	}
}

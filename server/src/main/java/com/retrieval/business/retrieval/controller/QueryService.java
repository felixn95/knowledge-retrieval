package com.retrieval.business.retrieval.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

import com.retrieval.business.retrieval.entity.ChatRequestDTO;
import com.retrieval.business.retrieval.entity.ChatResponseDTO;

@ApplicationScoped
public class QueryService {

	@Inject
	private ChatController chatController;

	public ChatResponseDTO processChat(@Valid ChatRequestDTO request) {
		return chatController.processChat(request);
	}
}

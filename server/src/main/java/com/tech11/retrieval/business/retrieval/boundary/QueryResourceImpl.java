package com.tech11.retrieval.business.retrieval.boundary;

import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import com.tech11.retrieval.business.retrieval.controller.QueryService;
import com.tech11.retrieval.business.retrieval.entity.ChatRequestDTO;
import com.tech11.retrieval.business.retrieval.entity.ChatResponseDTO;

@RequiredArgsConstructor
@RequestScoped
public class QueryResourceImpl implements QueryResource {

	private final QueryService queryService;

	@Override
	public ChatResponseDTO processChat(final ChatRequestDTO request) {
		return queryService.processChat(request);
	}
}

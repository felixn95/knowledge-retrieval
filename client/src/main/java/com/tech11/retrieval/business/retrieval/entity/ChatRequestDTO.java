package com.tech11.retrieval.business.retrieval.entity;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is used to store the chat request data.
 *
 * @author FelixNeubauer
 */
@Setter
@Getter
public class ChatRequestDTO {

	// The chat message
	private List<ChatMessage> messages;

	// Allow passing additional context
	private RequestContextDTO context;

	// Session identifier
	private String sessionId;

	// Domains of interest
	private DomainsOfInterestDTO domainsOfInterest;

}

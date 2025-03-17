package com.tech11.retrieval.business.retrieval.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ChatMessage {

	// role should be USER or ASSISTANT
	private ChatRole role;
	private String content;
}
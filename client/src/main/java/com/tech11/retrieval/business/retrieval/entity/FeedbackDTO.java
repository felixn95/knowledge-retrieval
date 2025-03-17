package com.tech11.retrieval.business.retrieval.entity;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDTO {

	// true if feedback is positive, false otherwise
	@NotNull
	private boolean positive;

	// optional free-text comment
	private String comment;
}

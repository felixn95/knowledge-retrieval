package com.tech11.retrieval.business.retrieval.entity;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QueryRequestDTO {

	// Optional sessionId; if null a new session is created.
	private String sessionId;

	@NotBlank(message = "Query must not be blank")
	private String query;

	List<String> documentNames;

}

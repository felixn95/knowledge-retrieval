package com.tech11.retrieval.business.retrieval.entity;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EmbedRequestDTO {

	@NotBlank(message = "Request text must not be blank")
	private String textChunk;

}

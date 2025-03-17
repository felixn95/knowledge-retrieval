package com.tech11.retrieval.business.retrieval.entity;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VectorSearchRequestDTO {

	@NotBlank(message = "Query must not be blank")
	private String query;

	@NotEmpty(message = "Document names must not be empty")
	private List<String> documentNames;

	@Min(value = 1, message = "Limit must be at least 1")
	private int limit;

	@NotNull(message = "Metric must not be null")
	private DistanceMetric metric;

}

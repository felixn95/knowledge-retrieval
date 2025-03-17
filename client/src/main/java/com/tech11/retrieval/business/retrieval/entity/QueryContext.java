package com.tech11.retrieval.business.retrieval.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryContext {
	private String historySummary;
	private String queryForVectorSearch;
}

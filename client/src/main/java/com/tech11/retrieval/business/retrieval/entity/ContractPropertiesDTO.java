package com.tech11.retrieval.business.retrieval.entity;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractPropertiesDTO {

	private String contractKey;
	private Map<String, Object> keyProperties;
}

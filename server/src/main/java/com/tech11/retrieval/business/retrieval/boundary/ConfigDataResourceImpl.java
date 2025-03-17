package com.tech11.retrieval.business.retrieval.boundary;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import com.tech11.retrieval.business.retrieval.controller.ConfigDataService;

@RequestScoped
public class ConfigDataResourceImpl implements ConfigDataResource {

	@Inject
	ConfigDataService configDataService;

	@Override
	public Response getConfigData(final String productKey, final String configType) {

		return configDataService.getConfigDataResponse(productKey, configType);
	}
}
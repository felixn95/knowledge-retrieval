package com.tech11.retrieval.business.retrieval.boundary;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Resource interface for configuration data.
 *
 * @author FelixNeubauer
 */
@Path("/configdata")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConfigDataResource {

	/**
	 * Returns a JSON configuration (e.g., product_documents_mapping.json or product_key_properties.json) given a
	 * productKey and configType.
	 *
	 * @param productKey
	 *     the product key (e.g. "au-03")
	 * @param configType
	 *     the config filename without extension (e.g. "product_documents_mapping")
	 * @return JSON config if found, otherwise 404
	 */
	@GET
	@Path("/{productKey}/{configType}")
	Response getConfigData(
			@PathParam("productKey") final String productKey,
			@PathParam("configType") final String configType);
}
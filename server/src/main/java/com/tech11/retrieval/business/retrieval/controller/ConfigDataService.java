package com.tech11.retrieval.business.retrieval.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ConfigDataService {

	@Inject
	@ConfigProperty(name = "documents.base-path")
	private String basePath;

	private static final Logger LOGGER = Logger.getLogger(ConfigDataService.class.getName());

	/**
	 * Returns a JAX-RS Response containing the JSON configuration. If the file is not found or cannot be read, an
	 * appropriate error response is returned.
	 *
	 * @param productKey
	 *     the product key
	 * @param configType
	 *     the type of configuration
	 * @return a Response containing the JSON config or an error message
	 */
	public Response getConfigDataResponse(final String productKey, final String configType) {
		Optional<String> jsonContent = loadConfigData(productKey, configType);

		if (jsonContent.isPresent()) {
			return Response.ok(jsonContent.get()).build();
		} else {
			String errorMsg = "Config file not found or could not be read: " + buildConfigPath(productKey, configType);
			return Response.status(Response.Status.NOT_FOUND).entity(errorMsg).build();
		}
	}

	/**
	 * Returns the JSON configuration as a String. This method throws an IOException if the config file is missing or
	 * cannot be read.
	 *
	 * @param productKey
	 *     the product key
	 * @param configType
	 *     the type of configuration
	 * @return the JSON configuration as String
	 * @throws IOException
	 *     if the file is not found or cannot be read
	 */
	public String getConfigDataJsonString(final String productKey, final String configType) throws IOException {
		Path configPath = buildConfigPath(productKey, configType);

		if (!Files.exists(configPath)) {
			throw new IOException("Config file not found: " + configPath);
		}

		return Files.readString(configPath);
	}

	/**
	 * Helper method that builds the config file path.
	 *
	 * @param productKey
	 *     the product key
	 * @param configType
	 *     the type of configuration
	 * @return the Path to the configuration file
	 */
	private Path buildConfigPath(final String productKey, final String configType) {
		return Paths.get(basePath, productKey, "mapping-configs", configType + ".json");
	}

	/**
	 * Loads the configuration file content as a JSON string.
	 *
	 * @param productKey
	 *     the product key
	 * @param configType
	 *     the type of configuration
	 * @return an Optional containing the JSON string if successful, or empty if not found or error occurs
	 */
	private Optional<String> loadConfigData(final String productKey, final String configType) {
		Path configPath = buildConfigPath(productKey, configType);

		if (!Files.exists(configPath)) {
			LOGGER.warning("Config file not found: " + configPath.toAbsolutePath());
			return Optional.empty();
		}

		try {
			return Optional.of(Files.readString(configPath));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error reading config file: " + configPath.toAbsolutePath(), e);
			return Optional.empty();
		}
	}
}
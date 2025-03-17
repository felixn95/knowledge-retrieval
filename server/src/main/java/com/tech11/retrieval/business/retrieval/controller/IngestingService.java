package com.tech11.retrieval.business.retrieval.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.tech11.retrieval.business.retrieval.entity.DocumentChunkDTO;

/**
 * Service for ingesting document content from files or folders.
 *
 * @author FelixNeubauer
 */
@ApplicationScoped
public class IngestingService {

	@ConfigProperty(name = "documents.base-path")
	String documentsBasePath;

	@Inject
	DocumentPersistenceService persistenceService;

	@Inject
	EmbeddingService embeddingService;

	// JSON-B instance instead of Jackson's ObjectMapper
	private static final Jsonb JSONB = JsonbBuilder.create();

	/**
	 * Processes a document chunk by first fetching the embedding (blocking call) outside of any transaction, then
	 * persisting it in its own transaction.
	 */
	public void processDocumentChunk(final DocumentChunkDTO docChunk) {
		// 1. Call the blocking embedding API outside any transaction.
		final float[] embedding = embeddingService.createEmbeddingAzureOpenAI(docChunk.getChunkContent());

		// 2. Persist the document chunk in a new transaction.
		persistenceService.persistDocumentChunk(docChunk, embedding);
	}

	/**
	 * Ingests content from a folder or a specific file under the configured base path. If the filename is null or
	 * blank, all *.json files in the folder are processed.
	 */
	public void ingestFromResource(final String folder, final String filename) {
		final Path folderPath = Paths.get(documentsBasePath, folder);

		if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
			throw new IllegalArgumentException("Folder does not exist: " + folderPath);
		}

		if (filename == null || filename.isBlank()) {
			// Process all JSON files in the folder
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath, "*.json")) {
				for (final Path filePath : stream) {
					final List<DocumentChunkDTO> chunks = parseJsonFile(filePath);
					for (DocumentChunkDTO chunk : chunks) {
						processDocumentChunk(chunk);
					}
				}
			} catch (IOException e) {
				throw new UncheckedIOException("Error reading folder: " + folderPath, e);
			}
		} else {
			// Process only the specified file
			final Path filePath = folderPath.resolve(filename);
			if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
				throw new IllegalArgumentException("File does not exist: " + filePath);
			}
			final List<DocumentChunkDTO> chunks = parseJsonFile(filePath);
			for (final DocumentChunkDTO chunk : chunks) {
				processDocumentChunk(chunk);
			}
		}
	}

	/**
	 * Parses a JSON file into a List of DocumentChunkDTO using JSON-B.
	 */
	private List<DocumentChunkDTO> parseJsonFile(final Path filePath) {
		if (!Files.exists(filePath)) {
			throw new IllegalArgumentException("File not found: " + filePath);
		}
		try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
			// Use reflection trick to get a Type for List<DocumentChunkDTO>.
			return JSONB.fromJson(
					reader,
					new ArrayList<DocumentChunkDTO>() {
					}.getClass().getGenericSuperclass());
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to parse JSON file: " + filePath, e);
		}
	}
}
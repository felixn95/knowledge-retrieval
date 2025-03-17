package com.tech11.retrieval.business.retrieval.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PromptResourceLoader {
	public static String loadResource(String path) {
		try {
			return Files.readString(Path.of(path), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Unable to load resource: " + path, e);
		}
	}
}

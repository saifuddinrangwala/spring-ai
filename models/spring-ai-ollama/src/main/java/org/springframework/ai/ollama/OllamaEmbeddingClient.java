/*
 * Copyright 2023-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.ollama;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.AbstractEmbeddingClient;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.EmbeddingRequest;
import org.springframework.ai.ollama.api.OllamaApiOptions;
import org.springframework.util.Assert;

/**
 * @author Christian Tzolov
 */
public class OllamaEmbeddingClient extends AbstractEmbeddingClient {

	private final OllamaApi ollamaApi;

	private String model = "orca-mini";

	private Map<String, Object> clientOptions;

	public OllamaEmbeddingClient(OllamaApi ollamaApi) {
		this.ollamaApi = ollamaApi;
	}

	public OllamaEmbeddingClient withModel(String model) {
		this.model = model;
		return this;
	}

	public OllamaEmbeddingClient withOptions(Map<String, Object> options) {
		this.clientOptions = options;
		return this;
	}

	public OllamaEmbeddingClient withOptions(OllamaApiOptions.Options options) {
		this.clientOptions = options.toMap();
		return this;
	}

	@Override
	public List<Double> embed(String text) {
		return this.embed(List.of(text)).iterator().next();
	}

	@Override
	public List<Double> embed(Document document) {
		return embed(document.getContent());
	}

	@Override
	public List<List<Double>> embed(List<String> texts) {
		Assert.notEmpty(texts, "At least one text is required!");
		Assert.isTrue(texts.size() == 1, "Ollama Embedding does not support batch embedding!");

		String inputContent = texts.iterator().next();

		OllamaApi.EmbeddingResponse response = this.ollamaApi
			.embeddings(new EmbeddingRequest(this.model, inputContent, this.clientOptions));

		return List.of(response.embedding());
	}

	@Override
	public EmbeddingResponse embedForResponse(List<String> texts) {
		var indexCounter = new AtomicInteger(0);
		List<Embedding> embeddings = this.embed(texts)
			.stream()
			.map(e -> new Embedding(e, indexCounter.getAndIncrement()))
			.toList();
		return new EmbeddingResponse(embeddings);
	}

}
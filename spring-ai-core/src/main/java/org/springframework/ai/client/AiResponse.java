/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.ai.client;

import org.springframework.ai.client.metadata.GenerationMetadata;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AiResponse {

	private final GenerationMetadata metadata;

	private final List<Generation> generations;

	private final Map<String, Object> providerOutput;

	private final Map<String, Object> runInfo;

	public AiResponse(List<Generation> generations) {
		this(generations, Collections.emptyMap(), Collections.emptyMap(), GenerationMetadata.EMPTY);
	}

	public AiResponse(List<Generation> generations, GenerationMetadata metadata) {
		this(generations, Collections.emptyMap(), Collections.emptyMap(), metadata);
	}

	public AiResponse(List<Generation> generations, Map<String, Object> providerOutput) {
		this(generations, providerOutput, Collections.emptyMap(), GenerationMetadata.EMPTY);
	}

	public AiResponse(List<Generation> generations, Map<String, Object> providerOutput, Map<String, Object> runInfo) {
		this(generations, providerOutput, runInfo, GenerationMetadata.EMPTY);
	}

	public AiResponse(List<Generation> generations, Map<String, Object> providerOutput, Map<String, Object> runInfo,
			GenerationMetadata metadata) {

		this.metadata = metadata;
		this.generations = List.copyOf(generations);
		this.providerOutput = Map.copyOf(providerOutput);
		this.runInfo = Map.copyOf(runInfo);
	}

	/**
	 * The {@link List} of {@link Generation generated outputs}.
	 * <p>
	 * It is a {@link List} of {@link List lists} because the Prompt could request
	 * multiple output {@link Generation generations}.
	 * @return the {@link List} of {@link Generation generated outputs}.
	 */
	public List<Generation> getGenerations() {
		return this.generations;
	}

	public Generation getGeneration() {
		return this.generations.get(0);
	}

	/**
	 * Returns {@link GenerationMetadata metadata} containing metadata about the use of
	 * the AI provider's API.
	 * @return {@link GenerationMetadata metadata} containing metadata about the use of
	 * the AI provider's API.
	 */
	public GenerationMetadata getMetadata() {
		return this.metadata;
	}

	/**
	 * Arbitrary model provider specific output
	 */
	public Map<String, Object> getProviderOutput() {
		return this.providerOutput;
	}

	/**
	 * The run metadata information
	 */
	public Map<String, Object> getRunInfo() {
		return this.runInfo;
	}

}

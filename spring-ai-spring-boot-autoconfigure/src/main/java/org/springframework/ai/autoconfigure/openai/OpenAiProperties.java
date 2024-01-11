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

package org.springframework.ai.autoconfigure.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(OpenAiProperties.CONFIG_PREFIX)
public class OpenAiProperties {

	public static final String CONFIG_PREFIX = "spring.ai.openai";

	private static final String BASE_URL = "https://api.openai.com";

	private Double temperature = 0.7;

	private final Embedding embedding = new Embedding(BASE_URL);

	private final Metadata metadata = new Metadata();

	private String apiKey;

	private String model = "gpt-3.5-turbo";

	private String baseUrl = BASE_URL;

	public String getApiKey() {
		return this.apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
		this.embedding.setApiKeyFromParent(apiKey);
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getBaseUrl() {
		return this.baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		this.embedding.setBaseUrlFromParent(baseUrl);
	}

	public Double getTemperature() {
		return this.temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Embedding getEmbedding() {
		return this.embedding;
	}

	public Metadata getMetadata() {
		return this.metadata;
	}

	public static class Embedding {

		private String apiKey;

		private String apiKeyFromParent;

		private String model = "text-embedding-ada-002";

		private String baseUrl;

		private String baseUrlFromParent;

		private Embedding(String baseUrl) {
			this.baseUrlFromParent = baseUrl;
		}

		public String getApiKey() {
			return StringUtils.hasText(this.apiKey) ? this.apiKey : apiKeyFromParent;
		}

		public void setApiKey(String embeddingApiKey) {
			this.apiKey = embeddingApiKey;
		}

		public String getModel() {
			return this.model;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public String getBaseUrl() {
			return StringUtils.hasText(this.baseUrl) ? this.baseUrl : baseUrlFromParent;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		private void setApiKeyFromParent(String apiKey) {
			this.apiKeyFromParent = apiKey;
		}

		private void setBaseUrlFromParent(String baseUrl) {
			this.baseUrlFromParent = baseUrl;
		}

	}

	public static class Metadata {

		private Boolean rateLimitMetricsEnabled;

		public boolean isRateLimitMetricsEnabled() {
			return Boolean.TRUE.equals(getRateLimitMetricsEnabled());
		}

		public Boolean getRateLimitMetricsEnabled() {
			return this.rateLimitMetricsEnabled;
		}

		public void setRateLimitMetricsEnabled(Boolean rateLimitMetricsEnabled) {
			this.rateLimitMetricsEnabled = rateLimitMetricsEnabled;
		}

	}

}

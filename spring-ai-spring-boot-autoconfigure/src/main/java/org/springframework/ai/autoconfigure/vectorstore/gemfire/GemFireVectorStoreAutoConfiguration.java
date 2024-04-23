/*
 * Copyright 2023-2024 the original author or authors.
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

package org.springframework.ai.autoconfigure.vectorstore.gemfire;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.GemFireVectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author Geet Rawat
 */
@AutoConfiguration
@ConditionalOnClass({ GemFireVectorStore.class, EmbeddingClient.class })
@EnableConfigurationProperties(GemFireVectorStoreProperties.class)
@ConditionalOnProperty(prefix = "spring.ai.vectorstore.gemfire", value = { "index-name" })
public class GemFireVectorStoreAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public GemFireVectorStore vectorStore(EmbeddingClient embeddingClient, GemFireVectorStoreProperties properties) {
		var config = new GemFireVectorStore.GemFireVectorStoreConfig();

		config.setHost(properties.getHost())
			.setIndexName(properties.getIndexName())
			.setPort(properties.getPort())
			.setBeamWidth(properties.getBeamWidth())
			.setMaxConnections(properties.getMaxConnections())
			.setBuckets(properties.getBuckets())
			.setVectorSimilarityFunction(properties.getVectorSimilarityFunction())
			.setFields(properties.getFields());
		return new GemFireVectorStore(config, embeddingClient);
	}

}
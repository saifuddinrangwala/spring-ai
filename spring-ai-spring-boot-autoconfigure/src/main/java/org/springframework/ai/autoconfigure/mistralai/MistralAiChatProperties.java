/*
 * Copyright 2024-204 the original author or authors.
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

package org.springframework.ai.autoconfigure.mistralai;

import org.springframework.ai.mistral.MistralAiChatOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(MistralAiChatProperties.CONFIG_PREFIX)
public class MistralAiChatProperties extends MistralAiParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.mistral.chat";

	public static final String DEFAULT_CHAT_MODEL = "mistral-tiny";

	private static final Double DEFAULT_TEMPERATURE = 0.7;

	private static final Boolean DEFAULT_SAFE_PROMPT = false;

	/**
	 * Enable OpenAI chat client.
	 */
	private boolean enabled = true;

	@NestedConfigurationProperty
	private MistralAiChatOptions options = MistralAiChatOptions.builder()
			.withModel(DEFAULT_CHAT_MODEL)
			.withTemperature(DEFAULT_TEMPERATURE.floatValue())
			.withSafePrompt(DEFAULT_SAFE_PROMPT)
			.build();

	public MistralAiChatOptions getOptions() {
		return options;
	}

	public void setOptions(MistralAiChatOptions options) {
		this.options = options;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
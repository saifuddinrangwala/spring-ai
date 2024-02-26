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

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(OpenAiConnectionProperties.CONFIG_PREFIX)
public class OpenAiConnectionProperties extends OpenAiParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.openai";

	public static final String DEFAULT_BASE_URL = "https://api.openai.com";

	public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofMinutes(1);

	public OpenAiConnectionProperties() {
		super.setBaseUrl(DEFAULT_BASE_URL);
		super.setReadTimeout(DEFAULT_READ_TIMEOUT);
	}

}

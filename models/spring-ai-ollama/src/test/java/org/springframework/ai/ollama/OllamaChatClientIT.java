package org.springframework.ai.ollama;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApiOptions;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.ListOutputParser;
import org.springframework.ai.parser.MapOutputParser;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.ai.prompt.SystemPromptTemplate;
import org.springframework.ai.prompt.messages.Message;
import org.springframework.ai.prompt.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Disabled("For manual smoke testing only.")
class OllamaChatClientIT {

	private static String MODEL = "mistral";

	private static final Log logger = LogFactory.getLog(OllamaChatClientIT.class);

	@Container
	static GenericContainer<?> ollamaContainer = new GenericContainer<>("ollama/ollama:0.1.15").withExposedPorts(11434);

	static String baseUrl;

	@BeforeAll
	public static void beforeAll() throws IOException, InterruptedException {
		logger.info("Start pulling the '" + MODEL + " ' model ... would take several minutes ...");
		ollamaContainer.execInContainer("ollama", "pull", MODEL);
		logger.info(MODEL + " pulling competed!");

		baseUrl = "http://" + ollamaContainer.getHost() + ":" + ollamaContainer.getMappedPort(11434);
	}

	@Autowired
	private OllamaChatClient client;

	@Test
	void roleTest() {
		Message systemMessage = new SystemPromptTemplate("""
				You are a helpful AI assistant. Your name is {name}.
				You are an AI assistant that helps people find information.
				Your name is {name}
				You should reply to the user's request with your name and also in the style of a {voice}.
				""").createMessage(Map.of("name", "Bob", "voice", "pirate"));

		UserMessage userMessage = new UserMessage("Tell me about 5 famous pirates from the Golden Age of Piracy.");

		Prompt prompt = new Prompt(List.of(userMessage, systemMessage));
		ChatResponse response = client.generate(prompt);
		assertThat(response.getGeneration().getContent()).contains("Blackbeard");
	}

	@Disabled("TODO: Fix the parser instructions to return the correct format")
	@Test
	void outputParser() {
		DefaultConversionService conversionService = new DefaultConversionService();
		ListOutputParser outputParser = new ListOutputParser(conversionService);

		String format = outputParser.getFormat();
		String template = """
				List five {subject}
				{format}
				""";
		PromptTemplate promptTemplate = new PromptTemplate(template,
				Map.of("subject", "ice cream flavors.", "format", format));
		Prompt prompt = new Prompt(promptTemplate.createMessage());
		Generation generation = this.client.generate(prompt).getGeneration();

		List<String> list = outputParser.parse(generation.getContent());
		assertThat(list).hasSize(5);
	}

	@Disabled("TODO: Fix the parser instructions to return the correct format")
	@Test
	void mapOutputParser() {
		MapOutputParser outputParser = new MapOutputParser();

		String format = outputParser.getFormat();
		String template = """
				Remove Markdown code blocks from the output.
				Provide me a List of {subject}
				{format}
				""";
		PromptTemplate promptTemplate = new PromptTemplate(template,
				Map.of("subject", "an array of numbers from 1 to 9 under they key name 'numbers'", "format", format));
		Prompt prompt = new Prompt(promptTemplate.createMessage());

		Generation generation = client.generate(prompt).getGeneration();

		Map<String, Object> result = outputParser.parse(generation.getContent());
		assertThat(result.get("numbers")).isEqualTo(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

	}

	record ActorsFilmsRecord(String actor, List<String> movies) {
	}

	@Disabled("TODO: Fix the parser instructions to return the correct format")
	@Test
	void beanOutputParserRecords() {

		BeanOutputParser<ActorsFilmsRecord> outputParser = new BeanOutputParser<>(ActorsFilmsRecord.class);

		String format = outputParser.getFormat();
		String template = """
				Generate the filmography of 5 movies for Tom Hanks.
				{format}
				Remove Markdown code blocks from the output.
				""";
		PromptTemplate promptTemplate = new PromptTemplate(template, Map.of("format", format));
		Prompt prompt = new Prompt(promptTemplate.createMessage());
		Generation generation = client.generate(prompt).getGeneration();

		ActorsFilmsRecord actorsFilms = outputParser.parse(generation.getContent());
		assertThat(actorsFilms.actor()).isEqualTo("Tom Hanks");
		assertThat(actorsFilms.movies()).hasSize(5);
	}

	@Disabled("TODO: Fix the parser instructions to return the correct format")
	@Test
	void beanStreamOutputParserRecords() {

		BeanOutputParser<ActorsFilmsRecord> outputParser = new BeanOutputParser<>(ActorsFilmsRecord.class);

		String format = outputParser.getFormat();
		String template = """
				Generate the filmography of 5 movies for Tom Hanks.
				{format}
				Remove Markdown code blocks from the output.
				""";
		PromptTemplate promptTemplate = new PromptTemplate(template, Map.of("format", format));
		Prompt prompt = new Prompt(promptTemplate.createMessage());

		String generationTextFromStream = client.generateStream(prompt)
			.collectList()
			.block()
			.stream()
			.map(ChatResponse::getGenerations)
			.flatMap(List::stream)
			.map(Generation::getContent)
			.collect(Collectors.joining());

		ActorsFilmsRecord actorsFilms = outputParser.parse(generationTextFromStream);

		assertThat(actorsFilms.actor()).isEqualTo("Tom Hanks");
		assertThat(actorsFilms.movies()).hasSize(5);
	}

	@SpringBootConfiguration
	public static class TestConfiguration {

		@Bean
		public OllamaApi ollamaApi() {
			return new OllamaApi(baseUrl);
		}

		@Bean
		public OllamaChatClient ollamaChat(OllamaApi ollamaApi) {
			return new OllamaChatClient(ollamaApi).withModel(MODEL)
				.withOptions(OllamaApiOptions.Options.builder().withTemperature(0.9f).build());
		}

	}

}
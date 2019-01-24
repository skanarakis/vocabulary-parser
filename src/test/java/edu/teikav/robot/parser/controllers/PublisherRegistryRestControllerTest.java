package edu.teikav.robot.parser.controllers;

import edu.teikav.robot.parser.domain.PublisherGrammar;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;
import edu.teikav.robot.parser.services.YAMLBasedPublisherGrammarRegistryImpl;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PublisherRegistryRestController.class)
public class PublisherRegistryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublisherGrammarRegistry registry;

    @After
    public void initializeRegistry() {
        registry.clean();
    }

    @Test
    public void whenNoPublishers_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/parser/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void whenRegisteringSinglePublisher_shouldReturnIt() throws Exception {
        mockMvc.perform(post("/parser/publishers")
                .contentType("text/plain;charset=UTF-8")
                .content(
                    "publisher:" + "\n" +
                    "  name: Publisher A" + "\n" +
                    "  description: PubA Description" + "\n" +
                    "  publicationYear: 2018" + "\n" +
                    "vocabularyTokensOrdering: GARBAGE TERM GRAMMAR_TYPE TRANSLATION EXAMPLE" + "\n" +
                    "vocabularyTokenSpecs:" + "\n" +
                    "  - tokenType: GARBAGE" + "\n" +
                    "    textPoints: 9" + "\n" +
                    "    language: ENGLISH" + "\n" +
                    "    color:" + "\n" +
                    "      red: 0" + "\n" +
                    "      green: 0" + "\n" +
                    "      blue: 0" + "\n" +
                    "    italicized: false" + "\n" +
                    "    bold: false" + "\n" +
                    "  - tokenType: TERM" + "\n" +
                    "    textPoints: 11" + "\n" +
                    "    language: ENGLISH" + "\n" +
                    "    color:" + "\n" +
                    "      red: 0" + "\n" +
                    "      green: 176" + "\n" +
                    "      blue: 80" + "\n" +
                    "    italicized: false" + "\n" +
                    "    bold: false" + "\n" +
                    "  - tokenType: GRAMMAR_TYPE" + "\n" +
                    "    textPoints: 11" + "\n" +
                    "    language: ENGLISH" + "\n" +
                    "    color:" + "\n" +
                    "      red: 0" + "\n" +
                    "      green: 0" + "\n" +
                    "      blue: 0" + "\n" +
                    "    italicized: false" + "\n" +
                    "    bold: false" + "\n" +
                    "  - tokenType: TRANSLATION" + "\n" +
                    "    textPoints: 11" + "\n" +
                    "    language: GREEK" + "\n" +
                    "    color:" + "\n" +
                    "      red: 0" + "\n" +
                    "      green: 0" + "\n" +
                    "      blue: 0" + "\n" +
                    "    italicized: false" + "\n" +
                    "    bold: false" + "\n" +
                    "  - tokenType: EXAMPLE" + "\n" +
                    "    textPoints: 11" + "\n" +
                    "    language: ENGLISH" + "\n" +
                    "    color:" + "\n" +
                    "      red: 0" + "\n" +
                    "      green: 0" + "\n" +
                    "      blue: 0" + "\n" +
                    "    italicized: true" + "\n" +
                    "grammarTermTypes:" + "\n" +
                    "  (n): NOUN" + "\n" +
                    "  (v): VERB" + "\n" +
                    "  (adj): ADJECTIVE" + "\n"
                ))
                .andExpect(status().isOk());

        mockMvc.perform(get("/parser/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tokens",
                        containsInAnyOrder("GARBAGE", "TERM" ,"GRAMMAR_TYPE", "TRANSLATION", "EXAMPLE")));
    }

    @Test
    public void whenRegisteringTwoPublishers_shouldReturnThemAll() throws Exception {
        mockMvc.perform(post("/parser/publishers")
                .contentType("text/plain;charset=UTF-8")
                .content(
                        "publisher:" + "\n" +
                                "  name: Publisher A" + "\n" +
                                "  description: PubA Description" + "\n" +
                                "  publicationYear: 2018" + "\n" +
                                "vocabularyTokensOrdering: GARBAGE TERM GRAMMAR_TYPE TRANSLATION EXAMPLE" + "\n" +
                                "vocabularyTokenSpecs:" + "\n" +
                                "  - tokenType: GARBAGE" + "\n" +
                                "    textPoints: 9" + "\n" +
                                "    language: ENGLISH" + "\n" +
                                "    color:" + "\n" +
                                "      red: 0" + "\n" +
                                "      green: 0" + "\n" +
                                "      blue: 0" + "\n" +
                                "    italicized: false" + "\n" +
                                "    bold: false" + "\n" +
                                "  - tokenType: TERM" + "\n" +
                                "    textPoints: 11" + "\n" +
                                "    language: ENGLISH" + "\n" +
                                "    color:" + "\n" +
                                "      red: 0" + "\n" +
                                "      green: 176" + "\n" +
                                "      blue: 80" + "\n" +
                                "    italicized: false" + "\n" +
                                "    bold: false" + "\n" +
                                "  - tokenType: GRAMMAR_TYPE" + "\n" +
                                "    textPoints: 11" + "\n" +
                                "    language: ENGLISH" + "\n" +
                                "    color:" + "\n" +
                                "      red: 0" + "\n" +
                                "      green: 0" + "\n" +
                                "      blue: 0" + "\n" +
                                "    italicized: false" + "\n" +
                                "    bold: false" + "\n" +
                                "  - tokenType: TRANSLATION" + "\n" +
                                "    textPoints: 11" + "\n" +
                                "    language: GREEK" + "\n" +
                                "    color:" + "\n" +
                                "      red: 0" + "\n" +
                                "      green: 0" + "\n" +
                                "      blue: 0" + "\n" +
                                "    italicized: false" + "\n" +
                                "    bold: false" + "\n" +
                                "  - tokenType: EXAMPLE" + "\n" +
                                "    textPoints: 11" + "\n" +
                                "    language: ENGLISH" + "\n" +
                                "    color:" + "\n" +
                                "      red: 0" + "\n" +
                                "      green: 0" + "\n" +
                                "      blue: 0" + "\n" +
                                "    italicized: true" + "\n" +
                                "grammarTermTypes:" + "\n" +
                                "  (n): NOUN" + "\n" +
                                "  (v): VERB" + "\n" +
                                "  (adj): ADJECTIVE" + "\n"
                ))
                .andExpect(status().isOk());

        mockMvc.perform(post("/parser/publishers")
                .contentType("text/plain;charset=UTF-8")
                .content(
                        "publisher:" + "\n" +
                                "  name: Publisher B" + "\n" +
                                "  description: PubB Description" + "\n" +
                                "  publicationYear: 2017" + "\n" +
                                "vocabularyTokensOrdering: TERM TRANSLATION GRAMMAR_TYPE" + "\n" +
                                "vocabularyTokenSpecs:" + "\n" +
                                "  - tokenType: TERM" + "\n" +
                                "    textPoints: 12" + "\n" +
                                "    language: ENGLISH" + "\n" +
                                "    color:" + "\n" +
                                "      red: 0" + "\n" +
                                "      green: 96" + "\n" +
                                "      blue: 120" + "\n" +
                                "    italicized: false" + "\n" +
                                "    bold: false" + "\n" +
                                "  - tokenType: TRANSLATION" + "\n" +
                                "    textPoints: 15" + "\n" +
                                "    language: GREEK" + "\n" +
                                "    color:" + "\n" +
                                "      red: 0" + "\n" +
                                "      green: 0" + "\n" +
                                "      blue: 0" + "\n" +
                                "    italicized: false" + "\n" +
                                "    bold: true" + "\n" +
                                "  - tokenType: GRAMMAR_TYPE" + "\n" +
                                "    textPoints: 10" + "\n" +
                                "    language: ENGLISH" + "\n" +
                                "    color:" + "\n" +
                                "      red: 0" + "\n" +
                                "      green: 0" + "\n" +
                                "      blue: 0" + "\n" +
                                "    italicized: true" + "\n" +
                                "    bold: false" + "\n" +
                                "grammarTermTypes:" + "\n" +
                                "  noun: NOUN" + "\n" +
                                "  verb: VERB" + "\n" +
                                "  adjective: ADJECTIVE" + "\n"
                ))
                .andExpect(status().isOk());

        mockMvc.perform(get("/parser/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].tokens",
                        containsInAnyOrder("GARBAGE", "TERM" ,"GRAMMAR_TYPE", "TRANSLATION", "EXAMPLE")))
                .andExpect(jsonPath("$[1].tokens",
                        containsInAnyOrder("TERM" ,"GRAMMAR_TYPE", "TRANSLATION")));
    }

    @TestConfiguration
    static class ParsingRestControllerTestConfig {

        @Bean
        public PublisherGrammarRegistry getRegistry() {
            return new YAMLBasedPublisherGrammarRegistryImpl(getYAML());
        }

        @Bean
        public Yaml getYAML() {
            return new Yaml(new Constructor(PublisherGrammar.class));
        }
    }
}
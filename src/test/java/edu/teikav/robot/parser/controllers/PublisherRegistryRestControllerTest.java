package edu.teikav.robot.parser.controllers;

import edu.teikav.robot.parser.domain.PublisherDocumentInput;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import edu.teikav.robot.parser.services.YAMLPublisherSpecRegistryImpl;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
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
    private PublisherSpecificationRegistry registry;

    @After
    public void initializeRegistry() {
        registry.removeAllPublisherSpecs();
    }

    @Test
    public void whenNoPublishers_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/registry/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void whenRegisteringSinglePublisher_shouldReturnIt() throws Exception {
        mockMvc.perform(post("/registry/publishers")
                .contentType("text/plain;charset=UTF-8")
                .content(
                    "publisher:\n" +
                    "  name: Publisher A\n" +
                    "  description: PubA Description\n" +
                    "  publicationYear: 2018\n" +
                    "vocabularyStructureSpecs:\n" +
                    "  - tokenType: TERM\n" +
                    "    rtfSpecs:\n" +
                    "      textPoints: 11\n" +
                    "      color:\n" +
                    "        red: 0\n" +
                    "        green: 0\n" +
                    "        blue: 0\n" +
                    "      italicized: false\n" +
                    "      bold: false\n" +
                    "    tokenTypeSpecs:\n" +
                    "      language: ENGLISH\n" +
                    "      potentiallyLast: false\n" +
                    "      potentiallyLast: false\n" +
                    "      potentiallyLast: false\n" +
                    "  - tokenType: GRAMMAR_TYPE\n" +
                    "    rtfSpecs:\n" +
                    "      textPoints: 11\n" +
                    "      color:\n" +
                    "        red: 0\n" +
                    "        green: 0\n" +
                    "        blue: 0\n" +
                    "      italicized: false\n" +
                    "      bold: false\n" +
                    "    tokenTypeSpecs:\n" +
                    "      language: ENGLISH\n" +
                    "      potentiallyLast: false\n" +
                    "      potentiallyLast: false\n" +
                    "      potentiallyLast: false\n" +
                    "      pattern: \"(^\\\\(v\\\\)|^\\\\(n\\\\)|^\\\\(phr v\\\\)|^\\\\(adj\\\\)).*\"\n" +
                    "      compositeSpecs:\n" +
                    "        parts:\n" +
                    "          - \"GRAMMAR_TYPE\"\n" +
                    "          - \"VERB_PARTICIPLES\"\n" +
                    "        splitPattern: \"\\\\s\\\\(\"\n" +
                    "  - tokenType: TRANSLATION\n" +
                    "    rtfSpecs:\n" +
                    "      textPoints: 11\n" +
                    "      color:\n" +
                    "        red: 0\n" +
                    "        green: 0\n" +
                    "        blue: 0\n" +
                    "      italicized: false\n" +
                    "      bold: false\n" +
                    "    tokenTypeSpecs:\n" +
                    "      language: GREEK\n" +
                    "      potentiallyLast: false\n" +
                    "      potentiallyLast: false\n" +
                    "      potentiallyLast: false\n" +
                    "      pattern: \"[\\\\s\\\\p{InGreek}]+\"\n" +
                    "  - tokenType: EXAMPLE\n" +
                    "    rtfSpecs:\n" +
                    "      textPoints: 11\n" +
                    "      color:\n" +
                    "        red: 0\n" +
                    "        green: 0\n" +
                    "        blue: 0\n" +
                    "      italicized: true\n" +
                    "      bold: false\n" +
                    "    tokenTypeSpecs:\n" +
                    "      language: ENGLISH\n" +
                    "      potentiallyLast: true\n" +
                    "      potentiallyLast: false\n" +
                    "      potentiallyLast: false\n" +
                    "vocabularyStructureTransitions:\n" +
                    "  - \"TERM -> GRAMMAR_TYPE\"\n" +
                    "  - \"TERM -> TRANSLATION\"\n" +
                    "  - \"GRAMMAR_TYPE -> TRANSLATION\"\n" +
                    "  - \"TRANSLATION -> EXAMPLE\"\n" +
                    "  - \"EXAMPLE -> TERM\"\n" +
                    "vocabularySpeechPartMappings:\n" +
                    "  (n): NOUN\n" +
                    "  (v): VERB\n" +
                    "  (adj): ADJECTIVE"
                ))
                .andExpect(status().isOk());

        mockMvc.perform(get("/registry/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].publisher.name", equalTo("Publisher A")))
                .andExpect(jsonPath("$[0].vocabularyTokenSpecs", hasSize(4)))
                .andExpect(jsonPath("$[0].speechPartsMap", hasEntry("(n)", "NOUN")))
                .andExpect(jsonPath("$[0].speechPartsMap", hasEntry("(v)", "VERB")))
                .andExpect(jsonPath("$[0].speechPartsMap", hasEntry("(adj)", "ADJECTIVE")));
    }

    @Test
    public void whenRegisteringTwoPublishers_shouldReturnThemAll() throws Exception {
        mockMvc.perform(post("/registry/publishers")
                .contentType("text/plain;charset=UTF-8")
                .content(
                        "publisher:\n" +
                                "  name: Publisher A\n" +
                                "  description: PubA Description\n" +
                                "  publicationYear: 2018\n" +
                                "vocabularyStructureSpecs:\n" +
                                "  - tokenType: TERM\n" +
                                "    rtfSpecs:\n" +
                                "      textPoints: 11\n" +
                                "      color:\n" +
                                "        red: 0\n" +
                                "        green: 0\n" +
                                "        blue: 0\n" +
                                "      italicized: false\n" +
                                "      bold: false\n" +
                                "    tokenTypeSpecs:\n" +
                                "      language: ENGLISH\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "  - tokenType: GRAMMAR_TYPE\n" +
                                "    rtfSpecs:\n" +
                                "      textPoints: 11\n" +
                                "      color:\n" +
                                "        red: 0\n" +
                                "        green: 0\n" +
                                "        blue: 0\n" +
                                "      italicized: false\n" +
                                "      bold: false\n" +
                                "    tokenTypeSpecs:\n" +
                                "      language: ENGLISH\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "      pattern: \"(^\\\\(v\\\\)|^\\\\(n\\\\)|^\\\\(phr v\\\\)|^\\\\(adj\\\\)).*\"\n" +
                                "      compositeSpecs:\n" +
                                "        parts:\n" +
                                "          - \"GRAMMAR_TYPE\"\n" +
                                "          - \"VERB_PARTICIPLES\"\n" +
                                "        splitPattern: \"\\\\s\\\\(\"\n" +
                                "  - tokenType: TRANSLATION\n" +
                                "    rtfSpecs:\n" +
                                "      textPoints: 11\n" +
                                "      color:\n" +
                                "        red: 0\n" +
                                "        green: 0\n" +
                                "        blue: 0\n" +
                                "      italicized: false\n" +
                                "      bold: false\n" +
                                "    tokenTypeSpecs:\n" +
                                "      language: GREEK\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "      pattern: \"[\\\\s\\\\p{InGreek}]+\"\n" +
                                "  - tokenType: EXAMPLE\n" +
                                "    rtfSpecs:\n" +
                                "      textPoints: 11\n" +
                                "      color:\n" +
                                "        red: 0\n" +
                                "        green: 0\n" +
                                "        blue: 0\n" +
                                "      italicized: true\n" +
                                "      bold: false\n" +
                                "    tokenTypeSpecs:\n" +
                                "      language: ENGLISH\n" +
                                "      potentiallyLast: true\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "vocabularyStructureTransitions:\n" +
                                "  - \"TERM -> GRAMMAR_TYPE\"\n" +
                                "  - \"TERM -> TRANSLATION\"\n" +
                                "  - \"GRAMMAR_TYPE -> TRANSLATION\"\n" +
                                "  - \"TRANSLATION -> EXAMPLE\"\n" +
                                "  - \"EXAMPLE -> TERM\"\n" +
                                "vocabularySpeechPartMappings:\n" +
                                "  (n): NOUN\n" +
                                "  (v): VERB\n" +
                                "  (adj): ADJECTIVE"
                ))
                .andExpect(status().isOk());

        mockMvc.perform(post("/registry/publishers")
                .contentType("text/plain;charset=UTF-8")
                .content(
                        "publisher:\n" +
                                "  name: Publisher B\n" +
                                "  description: PubB Description\n" +
                                "  publicationYear: 2019\n" +
                                "vocabularyStructureSpecs:\n" +
                                "  - tokenType: TERM\n" +
                                "    rtfSpecs:\n" +
                                "      textPoints: 11\n" +
                                "      color:\n" +
                                "        red: 0\n" +
                                "        green: 0\n" +
                                "        blue: 0\n" +
                                "      italicized: false\n" +
                                "      bold: false\n" +
                                "    tokenTypeSpecs:\n" +
                                "      language: ENGLISH\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "  - tokenType: GRAMMAR_TYPE\n" +
                                "    rtfSpecs:\n" +
                                "      textPoints: 12\n" +
                                "      color:\n" +
                                "        red: 0\n" +
                                "        green: 0\n" +
                                "        blue: 0\n" +
                                "      italicized: true\n" +
                                "      bold: false\n" +
                                "    tokenTypeSpecs:\n" +
                                "      language: ENGLISH\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "      pattern: \"(^\\\\(v\\\\)|^\\\\(n\\\\)|^\\\\(phr v\\\\)|^\\\\(adj\\\\)).*\"\n" +
                                "      compositeSpecs:\n" +
                                "        parts:\n" +
                                "          - \"GRAMMAR_TYPE\"\n" +
                                "          - \"VERB_PARTICIPLES\"\n" +
                                "        splitPattern: \"\\\\s\\\\(\"\n" +
                                "  - tokenType: TRANSLATION\n" +
                                "    rtfSpecs:\n" +
                                "      textPoints: 10\n" +
                                "      color:\n" +
                                "        red: 0\n" +
                                "        green: 0\n" +
                                "        blue: 0\n" +
                                "      italicized: false\n" +
                                "      bold: true\n" +
                                "    tokenTypeSpecs:\n" +
                                "      language: GREEK\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "      pattern: \"[\\\\s\\\\p{InGreek}]+\"\n" +
                                "  - tokenType: EXAMPLE\n" +
                                "    rtfSpecs:\n" +
                                "      textPoints: 11\n" +
                                "      color:\n" +
                                "        red: 0\n" +
                                "        green: 0\n" +
                                "        blue: 0\n" +
                                "      italicized: true\n" +
                                "      bold: false\n" +
                                "    tokenTypeSpecs:\n" +
                                "      language: ENGLISH\n" +
                                "      potentiallyLast: true\n" +
                                "      potentiallyLast: false\n" +
                                "      potentiallyLast: false\n" +
                                "vocabularyStructureTransitions:\n" +
                                "  - \"TERM -> GRAMMAR_TYPE\"\n" +
                                "  - \"TERM -> TRANSLATION\"\n" +
                                "  - \"GRAMMAR_TYPE -> TRANSLATION\"\n" +
                                "  - \"TRANSLATION -> EXAMPLE\"\n" +
                                "  - \"EXAMPLE -> TERM\"\n" +
                                "vocabularySpeechPartMappings:\n" +
                                "  (no): NOUN\n" +
                                "  (ver): VERB\n" +
                                "  (ad): ADJECTIVE"
                ))
                .andExpect(status().isOk());

        mockMvc.perform(get("/registry/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @TestConfiguration
    static class ParsingRestControllerTestConfig {

        @Bean
        public PublisherSpecificationRegistry getRegistry() {
            return new YAMLPublisherSpecRegistryImpl(getYAML());
        }

        @Bean
        public Yaml getYAML() {
            return new Yaml(new Constructor(PublisherDocumentInput.class));
        }
    }
}
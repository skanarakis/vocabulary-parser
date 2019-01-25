package edu.teikav.robot.parser.listeners;

import static org.mockito.ArgumentMatchers.any;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.domain.PublisherGrammarContext;
import edu.teikav.robot.parser.domain.TermGrammarTypes;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;

@RunWith(MockitoJUnitRunner.class)
public class VocabularySemanticsTokenizerTest {

    private VocabularySemanticsTokenizer tokenizer;

    private static PublisherGrammarRegistry registry;
    private static PublisherGrammarContext context;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private OutputStream outputStream;

    @BeforeClass
    public static void init() {
        registry = Mockito.mock(PublisherGrammarRegistry.class);
        context = Mockito.mock(PublisherGrammarContext.class);
        Mockito.when(registry.getActiveGrammarContext()).thenReturn(Optional.of(context));
    }

    @Before
    public void setup() throws XMLStreamException {
        tokenizer = new VocabularySemanticsTokenizer(registry, inventoryService, outputStream);
    }

    @Test
    public void acceptSingleTermWithoutFormatPeculiarities() {

        Mockito.when(context.vocabularyOrdering()).thenReturn(
                Arrays.asList("TERM", "GRAMMAR_TYPE", "TRANSLATION", "EXAMPLE")
        );
        Mockito.when(context.getGrammarTypeFor("(n)")).thenReturn(Optional.of(TermGrammarTypes.NOUN));

        String term = "rowing boat";
        String noun = "(n)";
        String termTranslation = "βάρκα κωπηλασίας";
        String termExample = "I like rowing with my new rowing boat!!";

        List<String> tokens = Arrays.asList(term, noun, termTranslation, termExample);

        tokens.forEach(tokenizer::processToken);

        InventoryItem item = new InventoryItem(term);
        item.setTermType(TermGrammarTypes.NOUN);
        item.setTranslation(termTranslation);
        item.setExample(termExample);
        Mockito.verify(inventoryService, Mockito.times(1))
                .saveNewInventoryItem(item);
    }

    @Test
    public void acceptTwoTermWithoutFormatPeculiarities() {

        Mockito.when(context.vocabularyOrdering()).thenReturn(
                Arrays.asList("TERM", "GRAMMAR_TYPE", "TRANSLATION", "EXAMPLE")
        );
        Mockito.when(context.getGrammarTypeFor("(n)")).thenReturn(Optional.of(TermGrammarTypes.NOUN));
        Mockito.when(context.getGrammarTypeFor("(v)")).thenReturn(Optional.of(TermGrammarTypes.VERB));

        String term = "rowing boat";
        String noun = "(n)";
        String termTranslation = "βάρκα κωπηλασίας";
        String termExample = "I like rowing with my new rowing boat!!";

        String term2 = "decide";
        String verb = "(v)";
        String term2Translation = "αποφασίζω";
        String term2Example = "Din't you decide yet?";

        List<String> tokens = Arrays.asList(term, noun, termTranslation, termExample,
                term2, verb, term2Translation, term2Example);

        tokens.forEach(tokenizer::processToken);

        Mockito.verify(inventoryService, Mockito.times(2))
                .saveNewInventoryItem(any(InventoryItem.class));
    }

}
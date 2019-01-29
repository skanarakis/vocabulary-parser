package edu.teikav.robot.parser.listeners;

import static org.mockito.ArgumentMatchers.any;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;

import org.junit.Assert;
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
public class VocabularyRecognizerStructureOneTest {

    private VocabularyRecognizer tokenizer;

    private static PublisherGrammarRegistry registry;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private OutputStream outputStream;

    @BeforeClass
    public static void init() {
        registry = Mockito.mock(PublisherGrammarRegistry.class);
        PublisherGrammarContext context = Mockito.mock(PublisherGrammarContext.class);

        Mockito.when(context.vocabularyOrdering()).thenReturn(
                Arrays.asList("TERM", "GRAMMAR_TYPE", "TRANSLATION", "EXAMPLE")
        );

        Mockito.when(context.isPartPotentiallyLast("TERM")).thenReturn(false);
        Mockito.when(context.isPartPotentiallyLast("GRAMMAR_TYPE")).thenReturn(false);
        Mockito.when(context.isPartPotentiallyLast("TRANSLATION")).thenReturn(false);
        Mockito.when(context.isPartPotentiallyLast("EXAMPLE")).thenReturn(true);

        Mockito.when(context.isPartPotentiallyComposite("GRAMMAR_TYPE")).thenReturn(true);
        Mockito.when(context.isPartPotentiallyComposite("TRANSLATION")).thenReturn(false);
        Mockito.when(context.isPartPotentiallyComposite("EXAMPLE")).thenReturn(false);

        Mockito.when(context.getCompositePartsFor("GRAMMAR_TYPE"))
                .thenReturn(Arrays.asList("GRAMMAR_TYPE", "VERB_PARTICIPLES"));
        Mockito.when(context.getCompositePartSplitToken("GRAMMAR_TYPE"))
                .thenReturn("\\s\\(");

        Mockito.when(context.patternOfToken("GRAMMAR_TYPE")).thenReturn("(^\\(v\\)|^\\(n\\)|^\\(phr v\\)|^\\(adj\\)).*");
        Mockito.when(context.patternOfToken("TRANSLATION")).thenReturn("[\\s\\p{InGreek}]+");

        Mockito.when(context.getStructureRelations()).thenReturn(
                Arrays.asList("TERM -> GRAMMAR_TYPE",
                        "TERM -> TRANSLATION",
                        "GRAMMAR_TYPE -> TRANSLATION",
                        "TRANSLATION -> EXAMPLE",
                        "EXAMPLE -> TERM")
        );

        Mockito.when(context.getGrammarTypeFor("(n)")).thenReturn(Optional.of(TermGrammarTypes.NOUN));
        Mockito.when(context.getGrammarTypeFor("(v)")).thenReturn(Optional.of(TermGrammarTypes.VERB));
        Mockito.when(context.getGrammarTypeFor("(adj)")).thenReturn(Optional.of(TermGrammarTypes.ADJECTIVE));

        Mockito.when(registry.getActiveGrammarContext()).thenReturn(Optional.of(context));
    }

    @Before
    public void setup() throws XMLStreamException {
        tokenizer = new VocabularyRecognizer(registry, inventoryService, outputStream);
    }

    @Test
    public void acceptSingleTermWithoutFormatPeculiarities() {

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
    public void acceptSingleTermWithCompositePart() {

        String term = "break";
        String compositePart = "(v) (past: broke, past part: broken)";
        String termTranslation = "σπάω";
        String termExample = "I broke my leg last week";

        String[] partsOfComposite = compositePart.split("\\s\\(");
        Assert.assertTrue(partsOfComposite.length == 2);

        List<String> tokens = Arrays.asList(term, compositePart, termTranslation, termExample);

        tokens.forEach(tokenizer::processToken);

        InventoryItem item = new InventoryItem(term);
        item.setTermType(TermGrammarTypes.VERB);
        item.setTranslation(termTranslation);
        item.setExample(termExample);
        item.setVerbParticiples(partsOfComposite[1]);
        Mockito.verify(inventoryService, Mockito.times(1))
                .saveNewInventoryItem(item);
    }

    @Test
    public void acceptTwoTermsWithoutFormatPeculiarities() {

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

    @Test
    public void acceptTwoTermsWithOptionalGrammarType() {

        String term = "rowing boat";
        String noun = "(n)";
        String termTranslation = "βάρκα κωπηλασίας";
        String termExample = "I like rowing with my new rowing boat!!";

        String term2 = "decide";
        String term2Translation = "αποφασίζω";
        String term2Example = "Didn't you decide yet?";

        List<String> tokens = Arrays.asList(term, noun, termTranslation, termExample,
                term2, term2Translation, term2Example);

        tokens.forEach(tokenizer::processToken);

        Mockito.verify(inventoryService, Mockito.times(2))
                .saveNewInventoryItem(any(InventoryItem.class));
    }

    @Test
    public void acceptFourTermsInTotalFirstTwoWithOptionalGrammarType() {

        String verb = "(v)";
        String adjective = "(adj)";

        String term = "our_adjective";
        String termTranslation = "μετάφραση ένα";
        String termExample = "our adjective example";

        String term2 = "our_noun";
        String term2Translation = "μετάφραση δύο";
        String term2Example = "noun 1 example";

        String term3 = "our_verb";
        String term3Translation = "μετάφραση τρία";
        String term3Example = "a verb example";

        String term4 = "our_adjective_again";
        String term4Translation = "μετάφραση τέσσερα";
        String term4Example = "adjective 2 example";

        List<String> tokens = Arrays.asList(term, termTranslation, termExample,
                term2, term2Translation, term2Example,
                term3, verb, term3Translation, term3Example,
                term4, adjective, term4Translation, term4Example);

        tokens.forEach(tokenizer::processToken);

        Mockito.verify(inventoryService, Mockito.times(4))
                .saveNewInventoryItem(any(InventoryItem.class));
    }
}
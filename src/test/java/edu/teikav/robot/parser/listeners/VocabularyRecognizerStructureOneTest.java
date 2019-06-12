package edu.teikav.robot.parser.listeners;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.domain.PublisherSpecification;
import edu.teikav.robot.parser.domain.SpeechPart;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class VocabularyRecognizerStructureOneTest {

    private VocabularyRecognizer recognizer;

    private static PublisherSpecificationRegistry registry;

    @Mock
    private InventoryService inventoryService;

    @BeforeClass
    public static void init() {
        registry = Mockito.mock(PublisherSpecificationRegistry.class);
        PublisherSpecification spec = Mockito.mock(PublisherSpecification.class);

        // Vocabulary Structure - Graph mock specs
        Mockito.when(spec.containsRootVocabularyToken("TERM")).thenReturn(true);
        Mockito.when(registry.getActiveSpec()).thenReturn(Optional.of(spec));

        Mockito.when(spec.getValidTransitionsFor("TERM"))
                .thenReturn(Arrays.asList("GRAMMAR_TYPE", "TRANSLATION"));
        Mockito.when(spec.getValidTransitionsFor("GRAMMAR_TYPE"))
                .thenReturn(Collections.singletonList("TRANSLATION"));
        Mockito.when(spec.getValidTransitionsFor("TRANSLATION"))
                .thenReturn(Collections.singletonList("EXAMPLE"));
        Mockito.when(spec.getValidTransitionsFor("EXAMPLE"))
                .thenReturn(Collections.singletonList("TERM"));

        // Vocabulary Structure - Token Types mock specs
        Mockito.when(spec.isTermPotentiallyLast("TERM")).thenReturn(false);
        Mockito.when(spec.isTermPotentiallySplit("TERM")).thenReturn(false);

        Mockito.when(spec.isTermPotentiallyLast("GRAMMAR_TYPE")).thenReturn(false);
        Mockito.when(spec.isTermPotentiallySplit("GRAMMAR_TYPE")).thenReturn(false);
        Mockito.when(spec.isTermPotentiallyComposite("GRAMMAR_TYPE")).thenReturn(true);
        Mockito.when(spec.getTermPattern("GRAMMAR_TYPE"))
                .thenReturn("(^\\(v\\)|^\\(n\\)|^\\(phr v\\)|^\\(adj\\)).*");
        Mockito.when(spec.getCompositePartsFor("GRAMMAR_TYPE"))
                .thenReturn(Arrays.asList("GRAMMAR_TYPE", "VERB_PARTICIPLES"));
        Mockito.when(spec.getCompositePartSplitToken("GRAMMAR_TYPE"))
                .thenReturn("\\s\\(");

        Mockito.when(spec.isTermPotentiallyLast("TRANSLATION")).thenReturn(false);
        Mockito.when(spec.isTermPotentiallySplit("TRANSLATION")).thenReturn(false);
        Mockito.when(spec.isTermPotentiallyComposite("TRANSLATION")).thenReturn(false);
        Mockito.when(spec.getTermPattern("TRANSLATION")).thenReturn("[\\s\\p{InGreek}]+");

        Mockito.when(spec.isTermPotentiallyLast("EXAMPLE")).thenReturn(true);
        Mockito.when(spec.isTermPotentiallySplit("EXAMPLE")).thenReturn(false);
        Mockito.when(spec.isTermPotentiallyComposite("EXAMPLE")).thenReturn(false);

        // Speech Parts mock specs
        Mockito.when(spec.getSpeechPartFor("(n)")).thenReturn(Optional.of(SpeechPart.NOUN));
        Mockito.when(spec.getSpeechPartFor("(v)")).thenReturn(Optional.of(SpeechPart.VERB));
        Mockito.when(spec.getSpeechPartFor("(adj)")).thenReturn(Optional.of(SpeechPart.ADJECTIVE));
    }

    @Before
    public void setup() {
        recognizer = new VocabularyRecognizer(registry, inventoryService);
    }

    @Test
    public void acceptSingleTermWithoutFormatPeculiarities() {

        String term = "rowing boat";
        String noun = "(n)";
        String termTranslation = "βάρκα κωπηλασίας";
        String termExample = "I like rowing with my new rowing boat!!";

        Stream<String> streamOfTokens = Stream.of(term, noun, termTranslation, termExample);

        recognizer.recognizeVocabulary(streamOfTokens);

        InventoryItem item = new InventoryItem(term);
        item.setTermType(SpeechPart.NOUN);
        item.setTranslation(termTranslation);
        item.setExample(termExample);
        Mockito.verify(inventoryService, Mockito.times(1))
                .save(item);
    }

    @Test
    public void acceptSingleTermWithCompositePart() {

        String term = "break";
        String compositePart = "(v) (past: broke, past part: broken)";
        String termTranslation = "σπάω";
        String termExample = "I broke my leg last week";

        String[] partsOfComposite = compositePart.split("\\s\\(");
        Assert.assertEquals(2, partsOfComposite.length);

        Stream<String> streamOfTokens = Stream.of(term, compositePart, termTranslation, termExample);

        recognizer.recognizeVocabulary(streamOfTokens);

        InventoryItem item = new InventoryItem(term);
        item.setTermType(SpeechPart.VERB);
        item.setTranslation(termTranslation);
        item.setExample(termExample);
        item.setVerbParticiples(partsOfComposite[1]);
        Mockito.verify(inventoryService, Mockito.times(1))
                .save(item);
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

        Stream<String> streamOfTokens = Stream.of(term, noun, termTranslation, termExample,
                term2, verb, term2Translation, term2Example);

        recognizer.recognizeVocabulary(streamOfTokens);

        Mockito.verify(inventoryService, Mockito.times(2))
                .save(any(InventoryItem.class));
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

        Stream<String> streamOfTokens = Stream.of(term, noun, termTranslation, termExample,
                term2, term2Translation, term2Example);

        recognizer.recognizeVocabulary(streamOfTokens);

        Mockito.verify(inventoryService, Mockito.times(2))
                .save(any(InventoryItem.class));
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

        Stream<String> streamOfTokens = Stream.of(term, termTranslation, termExample,
                term2, term2Translation, term2Example,
                term3, verb, term3Translation, term3Example,
                term4, adjective, term4Translation, term4Example);

        recognizer.recognizeVocabulary(streamOfTokens);

        Mockito.verify(inventoryService, Mockito.times(4))
                .save(any(InventoryItem.class));
    }
}
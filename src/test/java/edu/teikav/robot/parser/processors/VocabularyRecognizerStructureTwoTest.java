package edu.teikav.robot.parser.processors;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.domain.PublisherSpecification;
import edu.teikav.robot.parser.domain.SpeechPart;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
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
public class VocabularyRecognizerStructureTwoTest {

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
                .thenReturn(Arrays.asList("DERIVATIVES", "TERM"));
        Mockito.when(spec.getValidTransitionsFor("DERIVATIVES"))
                .thenReturn(Collections.singletonList("TERM"));

        // Vocabulary Structure - Token Types mock specs
        Mockito.when(spec.isTermPotentiallyLast("TERM")).thenReturn(false);
        Mockito.when(spec.isTermPotentiallySplit("TERM")).thenReturn(true);
        Mockito.when(spec.getTermPattern("TERM")).thenReturn("[a-zA-Z\\s\\(\\)]+");

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
        Mockito.when(spec.getTermPattern("TRANSLATION")).thenReturn("[-,\\(\\)\\s\\p{InGreek}]+");

        Mockito.when(spec.isTermPotentiallyLast("EXAMPLE")).thenReturn(true);
        Mockito.when(spec.isTermPotentiallySplit("EXAMPLE")).thenReturn(false);
        Mockito.when(spec.isTermPotentiallyComposite("EXAMPLE")).thenReturn(false);

        Mockito.when(spec.isTermPotentiallyLast("DERIVATIVES")).thenReturn(true);
        Mockito.when(spec.isTermPotentiallySplit("DERIVATIVES")).thenReturn(false);
        Mockito.when(spec.isTermPotentiallyComposite("DERIVATIVES")).thenReturn(false);
        Mockito.when(spec.getTermPattern("DERIVATIVES")).thenReturn("Der:[\\sa-zA-Z\\(\\)]+");

        // Speech Parts mock specs
        Mockito.when(spec.getSpeechPartFor("(n)")).thenReturn(Optional.of(SpeechPart.NOUN));
        Mockito.when(spec.getSpeechPartFor("(v)")).thenReturn(Optional.of(SpeechPart.VERB));
        Mockito.when(spec.getSpeechPartFor("(adj)")).thenReturn(Optional.of(SpeechPart.ADJECTIVE));
        Mockito.when(spec.getSpeechPartFor("(phr v)")).thenReturn(Optional.of(SpeechPart.PHRASAL_VERB));
    }

    @Before
    public void setup() {
        recognizer = new VocabularyRecognizer(registry, inventoryService);
    }

    @Test
    public void acceptSingleTermWithAllPartsInIt() {

        String term = "rowing boat";
        String noun = "(n)";
        String termTranslation = "βάρκα κωπηλασίας";
        String termExample = "I like rowing with my new rowing boat!!";
        String termDerivative = "Der: its derivative";

        Stream<String> streamOfTokens = Stream.of(term, noun, termTranslation, termExample, termDerivative);

        recognizer.recognizeVocabulary(streamOfTokens);

        InventoryItem item = new InventoryItem(term);
        item.setTermType(SpeechPart.NOUN);
        item.setTranslation(termTranslation);
        item.setExample(termExample);
        item.setDerivative(termDerivative);
        // One inventory item to save, but it will save it twice ... we cannot know term's delineation
        // if last term's part can be optional. So we have to save the term in every potential last part
        Mockito.verify(inventoryService, Mockito.times(2))
                .save(item);
    }

    @Test
    public void acceptFourTermsInTotalWithVariousStructures() {

        String verb = "(v)";
        String adjective = "(adj)";

        String term = "our adjective";
        String termTranslation = "μετάφραση ένα";
        String termExample = "our adjective example";
        String termDerivation = "Der: derivation one";

        String term2 = "our noun";
        String term2Translation = "μετάφραση δύο";
        String term2Example = "noun 1 example";

        String term3 = "our verb";
        String term3Translation = "μετάφραση τρία";
        String term3Example = "a verb example";

        String term4 = "our adjective again";
        String term4Translation = "μετάφραση τέσσερα";
        String term4Example = "adjective 2 example";
        String term4Derivation = "Der: derivation four";

        Stream<String> streamOfTokens = Stream.of(term, termTranslation, termExample, termDerivation,
                term2, term2Translation, term2Example,
                term3, verb, term3Translation, term3Example,
                term4, adjective, term4Translation, term4Example, term4Derivation);

        recognizer.recognizeVocabulary(streamOfTokens);
        // 4 vocabulary items, 6 actual saves
        Mockito.verify(inventoryService, Mockito.times(6))
                .save(any(InventoryItem.class));
    }

    @Test
    public void acceptStreamOfTermsWithMixedRules_TestA() {

        Stream<String> streamOfTokens = Stream.of(
                "configure", "(v)", "ρυθμίζω", "System was properly configured",
                "lighthouse", "(n)", "φάρος", "There was light at the top of the lighthouse",
                "mean", "(v) (past & past part: meant)", "εννοώ", "What do you mean?",
                "danger", "(n)", "κίνδυνος", "Sign said \"Danger! Do not enter\"", "Der: dangerous (adj)",
                "round", "(adj)", "στρογγυλός, -ή, -ό", "Ball is round",
                "(climb) stairs", "(n)", "(ανεβαίνω) σκαλοπάτια", "You have to climb the stairs to reach third floor!",
                "shine", "(v) (past & past part: shone)", "φωτίζω", "You shined the torch in my eyes", "Der: shining (n)",
                "horn", "(n)", "κόρνα", "Can you hear the ship horn?",
                "automatic", "(n)", "αυτόματος, -η, -ο", "Door is automatic",
                "mark", "(n)", "βαθμός (στο σχολείο)", "I get good marks in math",
                "come over", "(phr v)", "επισκέπτομαι, περνάω να δω κάποιον", "My brother came over to my house"
        );

        recognizer.recognizeVocabulary(streamOfTokens);

        Mockito.verify(inventoryService, Mockito.times(13))
                .save(any(InventoryItem.class));
    }

    @Test
    public void acceptStreamOfTermsWithMixedRules_TestB() {

        Stream<String> streamOfTokens = Stream.of(
                "termA", "(v)", "πρώτος όρος", "Term A Example",
                "termB", "(n)", "δεύτερος όρος", "Term B Example", "Der: derivative (v)",
                "(some)", "termC", "(adj)", "τρίτος όρος", "Term C Example"
        );

        recognizer.recognizeVocabulary(streamOfTokens);

        Mockito.verify(inventoryService, Mockito.times(4))
                .save(any(InventoryItem.class));
    }


}
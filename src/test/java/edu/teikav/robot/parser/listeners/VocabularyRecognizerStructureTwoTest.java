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
public class VocabularyRecognizerStructureTwoTest {

    private VocabularyRecognizer recognizer;

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
                Arrays.asList("TERM", "GRAMMAR_TYPE", "TRANSLATION", "EXAMPLE", "DERIVATIVES")
        );

        Mockito.when(context.isPartPotentiallyLast("TERM")).thenReturn(false);
        Mockito.when(context.isPartPotentiallyLast("GRAMMAR_TYPE")).thenReturn(false);
        Mockito.when(context.isPartPotentiallyLast("TRANSLATION")).thenReturn(false);
        Mockito.when(context.isPartPotentiallyLast("EXAMPLE")).thenReturn(true);
        Mockito.when(context.isPartPotentiallyLast("DERIVATIVES")).thenReturn(true);

        Mockito.when(context.isPartPotentiallySplit("TERM")).thenReturn(true);
        Mockito.when(context.isPartPotentiallySplit("GRAMMAR_TYPE")).thenReturn(false);
        Mockito.when(context.isPartPotentiallySplit("TRANSLATION")).thenReturn(false);
        Mockito.when(context.isPartPotentiallySplit("EXAMPLE")).thenReturn(false);
        Mockito.when(context.isPartPotentiallySplit("DERIVATIVES")).thenReturn(false);

        Mockito.when(context.isPartPotentiallyComposite("GRAMMAR_TYPE")).thenReturn(true);
        Mockito.when(context.isPartPotentiallyComposite("TRANSLATION")).thenReturn(false);
        Mockito.when(context.isPartPotentiallyComposite("EXAMPLE")).thenReturn(false);
        Mockito.when(context.isPartPotentiallyComposite("DERIVATIVES")).thenReturn(false);

        Mockito.when(context.getCompositePartsFor("GRAMMAR_TYPE"))
                .thenReturn(Arrays.asList("GRAMMAR_TYPE", "VERB_PARTICIPLES"));
        Mockito.when(context.getCompositePartSplitToken("GRAMMAR_TYPE"))
                .thenReturn("\\s\\(");

        Mockito.when(context.patternOfToken("TERM")).thenReturn("[a-zA-Z\\s\\(\\)]+");
        Mockito.when(context.patternOfToken("GRAMMAR_TYPE")).thenReturn("(^\\(v\\)|^\\(n\\)|^\\(phr v\\)|^\\(adj\\)).*");
        Mockito.when(context.patternOfToken("TRANSLATION")).thenReturn("[\\s\\p{InGreek}]+");
        Mockito.when(context.patternOfToken("DERIVATIVES")).thenReturn("Der:[\\sa-zA-Z\\(\\)]+");

        Mockito.when(context.getStructureRelations()).thenReturn(
                Arrays.asList("TERM -> GRAMMAR_TYPE",
                        "TERM -> TRANSLATION",
                        "GRAMMAR_TYPE -> TRANSLATION",
                        "TRANSLATION -> EXAMPLE",
                        "EXAMPLE -> DERIVATIVES",
                        "EXAMPLE -> TERM",
                        "DERIVATIVES -> TERM")
        );

        Mockito.when(context.getGrammarTypeFor("(n)")).thenReturn(Optional.of(TermGrammarTypes.NOUN));
        Mockito.when(context.getGrammarTypeFor("(v)")).thenReturn(Optional.of(TermGrammarTypes.VERB));
        Mockito.when(context.getGrammarTypeFor("(adj)")).thenReturn(Optional.of(TermGrammarTypes.ADJECTIVE));
        Mockito.when(context.getGrammarTypeFor("(phr v)")).thenReturn(Optional.of(TermGrammarTypes.PHRASAL_VERB));

        Mockito.when(registry.getActiveGrammarContext()).thenReturn(Optional.of(context));
    }

    @Before
    public void setup() throws XMLStreamException {
        recognizer = new VocabularyRecognizer(registry, inventoryService, outputStream);
    }

    @Test
    public void acceptSingleTermWithAllPartsInIt() {

        String term = "rowing boat";
        String noun = "(n)";
        String termTranslation = "βάρκα κωπηλασίας";
        String termExample = "I like rowing with my new rowing boat!!";
        String termDerivative = "Der: its derivative";

        List<String> tokens = Arrays.asList(term, noun, termTranslation, termExample, termDerivative);

        tokens.forEach(recognizer::processToken);

        InventoryItem item = new InventoryItem(term);
        item.setTermType(TermGrammarTypes.NOUN);
        item.setTranslation(termTranslation);
        item.setExample(termExample);
        item.setDerivative(termDerivative);
        // One inventory item to save, but it will save it twice ... we cannot know term's delineation
        // if last term's part can be optional. So we have to save the term in every potential last part
        Mockito.verify(inventoryService, Mockito.times(2))
                .saveNewInventoryItem(item);
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

        List<String> tokens = Arrays.asList(term, termTranslation, termExample, termDerivation,
                term2, term2Translation, term2Example,
                term3, verb, term3Translation, term3Example,
                term4, adjective, term4Translation, term4Example, term4Derivation);

        tokens.forEach(recognizer::processToken);
        // 4 vocabulary items, 6 actual saves
        Mockito.verify(inventoryService, Mockito.times(6))
                .saveNewInventoryItem(any(InventoryItem.class));
    }

    @Test
    public void acceptStreamOfTermsWithMixedRules_TestA() {

        List<String> streamOfTokens = Arrays.asList(
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

        streamOfTokens.forEach(recognizer::processToken);

        Mockito.verify(inventoryService, Mockito.times(13))
                .saveNewInventoryItem(any(InventoryItem.class));
    }

    @Test
    public void acceptStreamOfTermsWithMixedRules_TestB() {

        List<String> streamOfTokens = Arrays.asList(
                "termA", "(v)", "πρώτος όρος", "Term A Example",
                "termB", "(n)", "δεύτερος όρος", "Term B Example", "Der: derivative (v)",
                "(some)", "termC", "(adj)", "τρίτος όρος", "Term C Example"
        );

        streamOfTokens.forEach(recognizer::processToken);

        Mockito.verify(inventoryService, Mockito.times(4))
                .saveNewInventoryItem(any(InventoryItem.class));
    }


}
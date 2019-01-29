package edu.teikav.robot.parser.listeners;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.domain.PublisherGrammarContext;
import edu.teikav.robot.parser.domain.TermGrammarTypes;
import edu.teikav.robot.parser.domain.VocabularyToken;
import edu.teikav.robot.parser.domain.VocabularyTokenType;
import edu.teikav.robot.parser.exceptions.UnknownGrammarException;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;

@Component
@Qualifier("SecondPassParser")
public class VocabularyRecognizer extends AbstractRTFCommandsCallbackProcessor {

    private Logger logger = LoggerFactory.getLogger(VocabularyRecognizer.class);

    // Utilize policy rules of active grammar
    private PublisherGrammarRegistry registry;
    private PublisherGrammarContext grammarContext;

    private InventoryService inventoryService;

    private DefaultDirectedGraph<String, DefaultEdge> graph;
    private String activeVocabularyPart;
    private InventoryItem currentItem;

    private boolean isFirstStreamedToken = true;

    @FunctionalInterface
    private interface ItemUpdateSnippet {
       void updateItem(PublisherGrammarContext context, InventoryItem item, String value);
    }

    private static Map<String, ItemUpdateSnippet> itemUpdateSnippets;

    static {
        itemUpdateSnippets = new HashMap<>();
        itemUpdateSnippets.put("GRAMMAR_TYPE", (grammarContext, item, value) -> {
            // inventoryItemGrammarType - get it from the grammar context
            Optional<TermGrammarTypes> grammarType = grammarContext.getGrammarTypeFor(value);
            item.setTermType(grammarType.orElseThrow(() -> new RuntimeException("Inconsistency " +
                    "when fetching Grammar Type for >>" + value + "<<")));
        });
        itemUpdateSnippets.put("TRANSLATION", (grammarContext, item, value) -> item.setTranslation(value));
        itemUpdateSnippets.put("EXAMPLE", (grammarContext, item, value) -> item.setExample(value));
        itemUpdateSnippets.put("DERIVATIVES", (grammarContext, item, value) -> item.setDerivative(value));
        itemUpdateSnippets.put("VERB_PARTICIPLES", (grammarContext, item, value) -> item.setVerbParticiples(value));
    }

    VocabularyRecognizer(PublisherGrammarRegistry registry, InventoryService inventoryService,
                         @Qualifier("SecondPassOutputStream") OutputStream outputStream)
            throws XMLStreamException {

        super(outputStream);

        currentToken = new VocabularyToken();
        this.inventoryService = inventoryService;
        this.registry = registry;
    }

    @Override
    public void reset() {
        logger.info("Resetting {}", this.getClass().getName());
        this.grammarContext = null;
    }

    @Override
    public void processToken(String tokenString)
    {
        logger.debug("----\tProcessing token {}", tokenString);

        // The very first invocation of processString method will lead to
        // the construction of the recognition environment.
        // This will be done by the help of the necessary Grammar context.
        // Without it,vocabulary recognizer cannot complete its task.
        // In case no active grammar exists in the Publisher Grammar Registry
        // we throw an exception
        if (this.grammarContext == null) {
            createRecognitionEnvironment();
        }

        if (isFirstStreamedToken) {
            handleFirstStreamedToken(tokenString);
        } else {
            activeVocabularyPart = findNextVertexInVocabularyPartsGraph(tokenString);
        }

        processVocabularyPart(tokenString);

        if (grammarContext.isPartPotentiallyLast(activeVocabularyPart)) {
            inventoryService.saveNewInventoryItem(currentItem);
            logger.info("Inventory saving for {}", currentItem);
        }
    }

    private void createRecognitionEnvironment() {
        Optional<PublisherGrammarContext> grammarContextOptional = this.registry.getActiveGrammarContext();
        grammarContext = grammarContextOptional.orElseThrow(UnknownGrammarException::new);
        List<String> vocabularyParts = grammarContext.vocabularyOrdering();
        logger.info("Active Grammar has the following Vocabulary Terms Structure: \n\t{}", vocabularyParts);

        constructGraph(vocabularyParts);
        markRootVertex();
    }

    private void constructGraph(List<String> vocabularyParts) {
        // Construct the Graph
        graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        vocabularyParts.forEach(graph::addVertex);

        List<String> relations = grammarContext.getStructureRelations();
        for (String relation : relations) {
            String[] vertices = relation.split("->");
            graph.addEdge(vertices[0].trim(), vertices[1].trim());
        }
    }

    private void markRootVertex() {
        // Begin from the vertex showing the vocabulary term
        Optional<String> optionalTermVertex = graph.vertexSet().stream()
                .filter(v -> v.equals("TERM")).findAny();
        if (optionalTermVertex.isPresent()){
            activeVocabularyPart = optionalTermVertex.get();
        } else {
            throw new RuntimeException("GRAPH must have a TERM vertex");
        }
    }

    private void handleFirstStreamedToken(String tokenString) {
        isFirstStreamedToken = false;
        activeVocabularyPart = "TERM";
        logger.debug("Encountered 'TERM' vertex ... new vocabulary item");
        currentItem = new InventoryItem(tokenString);
    }

    private String findNextVertexInVocabularyPartsGraph(String tokenString) {
        Set<DefaultEdge> edges = graph.outgoingEdgesOf(activeVocabularyPart);

        for (DefaultEdge edge : edges) {
            if (edges.size() == 1) {
                String newVertex = graph.getEdgeTarget(edge);
                logger.debug("New VERTEX is {}", newVertex);
                return newVertex;
            }
            String relatedVertex = graph.getEdgeTarget(edge);
            Pattern pattern = Pattern.compile(grammarContext.patternOfToken(relatedVertex));
            if (pattern.matcher(tokenString).matches()) {
                logger.debug("Matched pattern {} for token {} (:{}). New Vertex is {}", grammarContext.patternOfToken(relatedVertex), tokenString,
                        relatedVertex, relatedVertex);
                return relatedVertex;
            } else {
                logger.debug("No match for pattern {} for token {} (:{})", grammarContext.patternOfToken(relatedVertex), tokenString,
                        relatedVertex);
            }
        }
        return "";
    }

    private void processVocabularyPart(String tokenString) {
        currentToken.setTokenType(VocabularyTokenType.valueOf(activeVocabularyPart));

        if (activeVocabularyPart.equals("TERM")) {
            currentItem = new InventoryItem(tokenString);
        } else {
            ItemUpdateSnippet snippet = itemUpdateSnippets.get(activeVocabularyPart);
            if (grammarContext.isPartPotentiallyComposite(activeVocabularyPart)) {
                String compositePartSplitToken = grammarContext.getCompositePartSplitToken(activeVocabularyPart);
                String[] partsOfComposite = tokenString.split(compositePartSplitToken);
                if (partsOfComposite.length == 1) {
                    // Not composite
                    snippet.updateItem(grammarContext, currentItem, tokenString);
                } else {
                    processCompositeVocabularyPart(partsOfComposite);
                }
            } else {
                snippet.updateItem(grammarContext, currentItem, tokenString);
            }
        }
    }

    private void processCompositeVocabularyPart(String[] compositeParts) {
        logger.debug("Entered composite section for {}", activeVocabularyPart);
        List<String> compositePartTypes = grammarContext.getCompositePartsFor(activeVocabularyPart);

        if (compositeParts.length != compositePartTypes.size()) {
            throw new RuntimeException("Segments of a composite vocabulary part must have corresponding split token segments");
        }

        for (int i = 0; i < compositePartTypes.size(); i++) {
            logger.debug("Iterating for segment {}({})", compositeParts[i], compositePartTypes.get(i));
            ItemUpdateSnippet snippet = itemUpdateSnippets.get(compositePartTypes.get(i));
            snippet.updateItem(grammarContext, currentItem, compositeParts[i]);
        }
    }
}

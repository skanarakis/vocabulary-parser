package edu.teikav.robot.parser.domain;

import edu.teikav.robot.parser.util.GenericUtils;
import edu.teikav.robot.parser.util.GraphUtils;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PublisherSpecification {

    private Logger logger = LoggerFactory.getLogger(PublisherSpecification.class);

    private static GraphUtils graphUtils = new GraphUtils();

    private Publisher publisher;
    private Set<Integer> specHashCodes;
    private List<VocabularyTokenSpecs> vocabularyTokenSpecs;
    private Map<String, SpeechPart> speechPartsMap;
    private DefaultDirectedGraph<String, DefaultEdge> graph;

    public PublisherSpecification(final PublisherDocumentInput document) {

        vocabularyTokenSpecs = new ArrayList<>();
        speechPartsMap = new HashMap<>();

        publisher = new Publisher(document.getPublisher());

        speechPartsMap = new HashMap<>(document.getVocabularySpeechPartMappings());

        document.getVocabularyStructureSpecs()
                .forEach(t -> vocabularyTokenSpecs.add(t.clone()));

        // Create the graph
        List<String> publisherSpecTokenTypes = vocabularyTokenSpecs.stream()
                .map(spec -> spec.getTokenType().toString()).collect(Collectors.toList());
        List<String> transitions = new ArrayList<>(document.getVocabularyStructureTransitions());
        graph = graphUtils.createNewGraph(publisherSpecTokenTypes, transitions);

        // Calculate the hash codes from the graph
        specHashCodes = calculateHashCodes(graph);
    }

    public Publisher getPublisher() {
        return new Publisher(publisher);
    }

    public boolean matchesAnyOfHashCodes(final int hashCode) {
        logger.debug("SPEC for {}: Stored Hash Codes are {}", publisher.getName(), specHashCodes);
        return specHashCodes.contains(hashCode);
    }

    public Optional<SpeechPart> getSpeechPartFor(final String input) {
        GenericUtils.validate(input, "Empty Speech Part input");
        SpeechPart speechPart = speechPartsMap.get(input);
        if (speechPart == null) {
            return Optional.empty();
        }
        return Optional.of(speechPart);
    }

    public List<VocabularyTokenSpecs> getFormatSpecsOfVocabularyTerms() {
        return Collections.unmodifiableList(vocabularyTokenSpecs);
    }

    public Set<Integer> getSpecHashCodes() {
        return Collections.unmodifiableSet(specHashCodes);
    }

    public List<VocabularyTokenSpecs> getVocabularyTokenSpecs() {
        return Collections.unmodifiableList(vocabularyTokenSpecs);
    }

    public Map<String, SpeechPart> getSpeechPartsMap() {
        return Collections.unmodifiableMap(speechPartsMap);
    }

    public String getTermPattern(final String type) {
        GenericUtils.validate(type, "Empty Token Type given as input");
        VocabularyTokenSpecs specs = getTokenSpecs(type);
        return specs.getTokenTypeSpecs().getPattern();
    }

    public boolean isTermPotentiallyLast(final String type) {
        GenericUtils.validate(type, "Empty Token Type given as input");
        VocabularyTokenSpecs specs = getTokenSpecs(type);
        return specs.getTokenTypeSpecs().isPotentiallyLast();
    }

    public boolean isTermPotentiallySplit(final String type) {
        GenericUtils.validate(type, "Empty Token Type given as input");
        VocabularyTokenSpecs specs = getTokenSpecs(type);
        return specs.getTokenTypeSpecs().isPotentiallySplit();
    }

    public boolean isTermPotentiallyComposite(final String type) {
        GenericUtils.validate(type, "Empty Token Type given as input");
        VocabularyTokenSpecs specs = getTokenSpecs(type);
        return specs.getTokenTypeSpecs().isPotentiallyComposite();
    }

    public List<String> getCompositePartsFor(final String type) {
        GenericUtils.validate(type, "Empty Token Type given as input");
        VocabularyTokenSpecs specs = getTokenSpecs(type);
        VocabularyTokenSpecs.TokenTypeSpecs.CompositeSpecs compositeSpecs =
                specs.getTokenTypeSpecs().getCompositeSpecs();
        if (compositeSpecs == null) {
            throw new NullPointerException("Composite specs are not present");
        }
        return compositeSpecs.getParts();
    }

    public String getCompositePartSplitToken(final String type) {
        GenericUtils.validate(type, "Empty Token Type given as input");
        VocabularyTokenSpecs specs = getTokenSpecs(type);
        VocabularyTokenSpecs.TokenTypeSpecs.CompositeSpecs compositeSpecs =
                specs.getTokenTypeSpecs().getCompositeSpecs();
        if (compositeSpecs == null) {
            throw new NullPointerException("Composite specs are not present");
        }
        return compositeSpecs.getSplitPattern();
    }

    public boolean containsRootVocabularyToken(String rootVertex) {
        return graphUtils.containsVertexOf(graph, rootVertex);
    }

    public List<String> getValidTransitionsFor(final String startTokenType) {

        GenericUtils.validate(startTokenType, "Empty vocabulary part given as input");

        Set<DefaultEdge> edges = graph.edgesOf(startTokenType);
        if (edges.isEmpty()) {
            throw new RuntimeException("No edges found for vertex " + startTokenType);
        }

        List<String> results = new ArrayList<>();
        edges.stream()
                // We only want edges that have startTokenType as the source vertex
                .filter(edge -> graph.getEdgeSource(edge).equals(startTokenType))
                .forEach(edge -> results.add(graph.getEdgeTarget(edge)));
        return results;
    }

    private Set<Integer> calculateHashCodes(DefaultDirectedGraph<String, DefaultEdge> graph) {
        // Each path of the vocabulary structure transitions will have a unique hash code based on the
        // traversed vocabulary parts. Hash code of each part contributes to the path hash code.
        // At the end, the publisher specification will have a set of potential hash codes to be used
        // in publisher identification
        Set<Integer> hashCodes = new HashSet<>();
        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "TERM");
        paths.forEach(path -> hashCodes.add(calculatePathHashCode(path)));
        return hashCodes;
    }

    private int calculatePathHashCode(GraphPath<String, DefaultEdge> path) {
        List<VocabularyTokenSpecs> formatSpecsForPath = new ArrayList<>();
        path.getEdgeList().stream()
                .map(this::getFirstVertexOfEdge)
                .forEach(vertexName -> formatSpecsForPath.add(getFormatSpecFor(vertexName)));
        logger.debug("Path : {}\nElements contributing to hash code: {}", path, formatSpecsForPath);
        return Objects.hash(formatSpecsForPath);
    }

    private String getFirstVertexOfEdge(DefaultEdge edge) {
        return graphUtils.getVerticesOfEdge(edge)[0];
    }

    private VocabularyTokenSpecs getFormatSpecFor(String type) {
        return vocabularyTokenSpecs.stream()
                .filter(spec -> spec.getTokenType().toString().equals(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No format specification found for type " + type)
        );
    }

    private VocabularyTokenSpecs getTokenSpecs(String type) {
        return vocabularyTokenSpecs.stream()
                .filter(spec -> spec.getTokenType().toString().equals(type)).findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find Spec for " + type));
    }

    public int getMaxWordsFor(final String type) {
        GenericUtils.validate(type, "Empty Token Type given as input");
        VocabularyTokenSpecs specs = getTokenSpecs(type);
        return specs.getTokenTypeSpecs().getMaxWords();
    }

    public int getMinWordsFor(final String type) {
        GenericUtils.validate(type, "Empty Token Type given as input");
        VocabularyTokenSpecs specs = getTokenSpecs(type);
        return specs.getTokenTypeSpecs().getMinWords();
    }
}

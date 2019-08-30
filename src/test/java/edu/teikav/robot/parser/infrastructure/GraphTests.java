package edu.teikav.robot.parser.infrastructure;

import edu.teikav.robot.parser.util.GraphUtils;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("Unit-Test: 3rd party Graph library")
class GraphTests {

    private GraphUtils graphUtils = new GraphUtils();

    @Test
    void findAllPathsNoVertexLoopExample1() {

        List<String> parts = Arrays.asList("TERM", "GRAMMAR_TYPE", "TRANSLATION", "EXAMPLE");
        List<String> transitions = Arrays.asList("TERM -> GRAMMAR_TYPE",
                "TERM -> TRANSLATION",
                "GRAMMAR_TYPE -> TRANSLATION",
                "TRANSLATION -> EXAMPLE",
                "EXAMPLE -> TERM");

        DefaultDirectedGraph<String, DefaultEdge> graph = constructGraph(parts, transitions);
        log.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "TERM");
        log.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertThat(paths.size()).isEqualTo(2);
    }

    @Test
    void findAllPathsNoVertexLoopExample2() {

        List<String> parts = Arrays.asList("A", "B", "C", "D", "E");
        List<String> transitions = Arrays.asList("A -> B",
                "A -> C",
                "B -> C",
                "C -> D",
                "D -> A",
                "D -> E",
                "E -> A");

        DefaultDirectedGraph<String, DefaultEdge> graph = constructGraph(parts, transitions);
        log.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "A");
        log.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertThat(paths.size()).isEqualTo(4);
    }

    @Test
    void findAllPathsNoVertexLoopExample3() {

        List<String> parts = Arrays.asList("A", "B", "C", "D", "E", "F");
        List<String> transitions = Arrays.asList("A -> B",
                "B -> C",
                "B -> C",
                "C -> D",
                "C -> E",
                "D -> A",
                "D -> F",
                "E -> A",
                "E -> F",
                "F -> A"
        );

        DefaultDirectedGraph<String, DefaultEdge> graph = constructGraph(parts, transitions);
        log.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "A");
        log.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertThat(paths.size()).isEqualTo(4);
    }

    @Test
    void findAllPathsWithVertexLoopExample1() {

        List<String> parts = Arrays.asList("A", "B", "C", "D");
        List<String> transitions = Arrays.asList("A -> B",
                "B -> B",
                "B -> C",
                "C -> D",
                "D -> A"
        );

        DefaultDirectedGraph<String, DefaultEdge> graph = constructGraph(parts, transitions);
        log.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "A",3);
        log.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertThat(paths.size()).isEqualTo(4);
    }

    @Test
    void findAllPathsWithVertexLoopExample2() {

        List<String> parts = Arrays.asList("A", "B", "C", "D", "E");
        List<String> transitions = Arrays.asList("A -> B",
                "A -> C",
                "B -> C",
                "C -> C",
                "C -> D",
                "D -> A",
                "D -> E",
                "E -> A");

        DefaultDirectedGraph<String, DefaultEdge> graph = constructGraph(parts, transitions);
        log.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "A", 3);
        log.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertThat(paths.size()).isEqualTo(16);
    }

    @Test
    void findAllPathsWithVertexLoopExample3() {

        List<String> parts = Arrays.asList("A", "B", "C");
        List<String> transitions = Arrays.asList("A -> B",
                "B -> A",
                "B -> B",
                "B -> C",
                "C -> A");

        DefaultDirectedGraph<String, DefaultEdge> graph = constructGraph(parts, transitions);
        log.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "A", 3);
        log.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertThat(paths.size()).isEqualTo(8);
    }

    private DefaultDirectedGraph<String, DefaultEdge> constructGraph(List<String> vocabularyParts,
                                                                     List<String> transitions) {
        // Construct the Graph
        DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        vocabularyParts.forEach(graph::addVertex);

        for (String transition : transitions) {
            String[] vertices = transition.split("->");
            graph.addEdge(vertices[0].trim(), vertices[1].trim());
        }
        return graph;
    }
}

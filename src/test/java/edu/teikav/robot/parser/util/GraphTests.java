package edu.teikav.robot.parser.util;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GraphTests {

    private Logger logger = LoggerFactory.getLogger(GraphTests.class);
    private GraphUtils graphUtils = new GraphUtils();

    @Test
    public void findAllPathsNoVertexLoopExample1() {

        List<String> parts = Arrays.asList("TERM", "GRAMMAR_TYPE", "TRANSLATION", "EXAMPLE");
        List<String> transitions = Arrays.asList("TERM -> GRAMMAR_TYPE",
                "TERM -> TRANSLATION",
                "GRAMMAR_TYPE -> TRANSLATION",
                "TRANSLATION -> EXAMPLE",
                "EXAMPLE -> TERM");

        DefaultDirectedGraph<String, DefaultEdge> graph = constructGraph(parts, transitions);
        logger.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "TERM");
        logger.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertEquals(2, paths.size());
    }

    @Test
    public void findAllPathsNoVertexLoopExample2() {

        List<String> parts = Arrays.asList("A", "B", "C", "D", "E");
        List<String> transitions = Arrays.asList("A -> B",
                "A -> C",
                "B -> C",
                "C -> D",
                "D -> A",
                "D -> E",
                "E -> A");

        DefaultDirectedGraph<String, DefaultEdge> graph = constructGraph(parts, transitions);
        logger.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "A");
        logger.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertEquals(4, paths.size());
    }

    @Test
    public void findAllPathsNoVertexLoopExample3() {

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
        logger.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "A");
        logger.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertEquals(4, paths.size());
    }

    @Test
    public void findAllPathsWithVertexLoopExample1() {

        List<String> parts = Arrays.asList("A", "B", "C", "D");
        List<String> transitions = Arrays.asList("A -> B",
                "B -> B",
                "B -> C",
                "C -> D",
                "D -> A"
        );

        DefaultDirectedGraph<String, DefaultEdge> graph = constructGraph(parts, transitions);
        logger.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "A",3);
        logger.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertEquals(4, paths.size());
    }

    @Test
    public void findAllPathsWithVertexLoopExample2() {

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
        logger.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "A", 3);
        logger.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertEquals(16, paths.size());
    }

    @Test
    public void findAllPathsWithVertexLoopExample3() {

        List<String> parts = Arrays.asList("A", "B", "C");
        List<String> transitions = Arrays.asList("A -> B",
                "B -> A",
                "B -> B",
                "B -> C",
                "C -> A");

        DefaultDirectedGraph<String, DefaultEdge> graph = constructGraph(parts, transitions);
        logger.info("Graph: {}", graph.toString());

        List<GraphPath<String, DefaultEdge>> paths = graphUtils.findAllPaths(graph, "A", 3);
        logger.info("Number of paths: {}\nPaths: {}", paths.size(), paths);
        assertEquals(8, paths.size());
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

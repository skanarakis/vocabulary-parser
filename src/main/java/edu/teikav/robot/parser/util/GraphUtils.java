package edu.teikav.robot.parser.util;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphUtils {

    // TODO: Currently implemented and tested for Graphs with single vertex loop. If there is any need for having
    // TODO: multiple vertices having loop in themselves, this code must be reworked

    public DefaultDirectedGraph<String, DefaultEdge> createNewGraph(List<String> vertices,
                                                                    List<String> transitions) {

        if (StringUtils.isEmpty(vertices)) {
            throw new IllegalArgumentException("Cannot create a graph without vertices");
        }
        if (StringUtils.isEmpty(transitions)) {
            throw new IllegalArgumentException("Cannot create a graph without edges");
        }
        // Construct the Graph
        DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        vertices.forEach(graph::addVertex);

        for (String transition : transitions) {
            String[] vert = transition.split("->");
            graph.addEdge(vert[0].trim(), vert[1].trim());
        }

        return graph;
    }

    public boolean containsVertexOf(DefaultDirectedGraph<String, DefaultEdge> graph, String vertexName) {
        Optional<String> optVertex = graph.vertexSet().stream()
                .filter(v -> v.equals(vertexName)).findAny();
        return optVertex.isPresent();
    }

    public List<GraphPath<String, DefaultEdge>> findAllPaths(DefaultDirectedGraph<String, DefaultEdge> graph,
                                                              String rootVertex) {
        return findAllPaths(graph, rootVertex, 0);
    }

    public List<GraphPath<String, DefaultEdge>> findAllPaths(DefaultDirectedGraph<String, DefaultEdge> graph,
                                                              String rootVertex,
                                                              int allowedVertexLoops) {
        int maxPathSizeWithoutVertexLoops = graph.vertexSet().size() + 1;
        int maxNumberOfVerticesInPath = maxPathSizeWithoutVertexLoops + allowedVertexLoops;
        return new AllDirectedPaths<>(graph)
                .getAllPaths(rootVertex, rootVertex, false, maxNumberOfVerticesInPath)
                .stream()
                .filter(path -> path.getVertexList().size() != 1)
                .filter(this::hasPathUniqueEdges)
                .filter(path -> vertexLoopLessThan(path, allowedVertexLoops))
                .collect(Collectors.toList());
    }

    public String[] getVerticesOfEdge(DefaultEdge edge) {
        return edge.toString()
                .replace("(", "")
                .replace(")", "")
                .split(" : ");
    }

    private boolean hasPathUniqueEdges(final GraphPath<String, DefaultEdge> path) {
        final List<DefaultEdge> edgeList = path.getEdgeList();

        final Set<String> uniqueTransitions = new HashSet<>();

        for (DefaultEdge e : edgeList) {
            // Transform (A : B) to just 'A' and 'B'
            final String[] verticesOfEdge = getVerticesOfEdge(e);
            if (uniqueTransitions.contains(e.toString()) && !verticesOfEdge[0].equals(verticesOfEdge[1])) {
                return false;
            }
            uniqueTransitions.add(e.toString());
        }
        return true;
    }

    private boolean vertexLoopLessThan(final GraphPath<String, DefaultEdge> path, final int loopTimes) {
        if (loopTimes < 2) {
            return true;
        }
        final List<DefaultEdge> edgeList = path.getEdgeList();

        Map<DefaultEdge, Long> collect = edgeList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return collect.values().stream().noneMatch(count -> count > loopTimes);
    }
}

package Routing.Algorithm;

import Routing.Graph.StopGraph;
import Routing.Graph.TravelInformation;

import java.util.*;

public interface ShortestPathAlgorithm {
    /**
     * A method to generate paths from starting point that reach end
     * @param start start point name
     * @param end end point name
     * @param graph graph
     * @param startTime starting time
     * @return all paths from starting point until it reaches the end
     */
    Map<String,TravelInformation> generatePaths(String start, String end, StopGraph graph, int startTime, Set<String> closures);

    /**
     * A method that returns path using {@link #generatePaths(String, String, StopGraph, int, Set<String>) generate paths}
     * And then recursively restores optimal path from start to end using linked list
     * @param start start point name
     * @param end end point name
     * @param graph graph
     * @param startTime starting time
     * @return optimal path from start point to end point
     */
    LinkedList<TravelInformation> getShortestPath(String start, String end, StopGraph graph,int startTime);

    void generateAllPaths(TravelInformation startInfo, PriorityQueue<TravelInformation> queue, StopGraph graph, Map<String, TravelInformation> uniqueTravelInfo);
}

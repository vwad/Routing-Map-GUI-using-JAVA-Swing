package Routing.Engine;

import Routing.Algorithm.Dijkstra;
import Routing.Algorithm.DijkstraHeatMap;
import Routing.Algorithm.DijkstraRouting;
import Routing.Graph.StopGraph;
import Routing.Graph.TravelInformation;
import Utils.Time.DateCalculator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class ClosuresDijkstraStarter extends DijkstraStarter{

    public LinkedList<TravelInformation> getRouteFromToWithClosures(StopGraph graph, String startTime, Set<String> closures)
    {
        Dijkstra dijkstra = new DijkstraRouting();
        dijkstra.addClosures(closures);
        int startTimeSec = DateCalculator.formatTimeStringToSec(startTime);
        return dijkstra.getShortestPath("start","end",graph,startTimeSec);
    }
}

package Routing.Engine;

import Routing.Graph.StopGraph;
import Routing.Graph.TravelInformation;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public interface RoutingStarter {
    public LinkedList<TravelInformation> getRouteFromTo(StopGraph graph, String startTime);
    public Map<String, TravelInformation> getForHeatMap(StopGraph graph, String startTime, Set<String> closures);
}

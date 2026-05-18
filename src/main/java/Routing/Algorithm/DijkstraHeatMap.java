package Routing.Algorithm;

import Routing.Graph.StopGraph;
import Routing.Graph.TravelInformation;
import Routing.Graph.WalkingTravelInformation;
import java.util.*;
import static GUI.Application.HeatMapCells.getMaxValueSeconds;

public class DijkstraHeatMap extends Dijkstra{

    @Override
    public Map<String, TravelInformation> generatePaths(String start, String end, StopGraph graph, int startTime, Set<String> closures) {
        TravelInformation startInfo = new WalkingTravelInformation(0,graph.getStop(start), graph.getStop(start));
        startInfo.setDepartureTime(startTime);
        addClosures(closures);
        return generatePaths(startInfo,graph,start,this);
    }

    public void generateAllPaths(TravelInformation startInfo, PriorityQueue<TravelInformation> queue, StopGraph graph, Map<String, TravelInformation> uniqueTravelInfo)
    {

        double startTime = startInfo.getArrivalTime()-startInfo.getArrivalTime()%60;

        while (!queue.isEmpty()) {

            TravelInformation travelInfo = queue.poll();
            double currentTime = travelInfo.getArrivalTime();
            String currentStop = travelInfo.getArrivalPoint();

            if(visited.contains(currentStop))
                continue;

            if(currentTime-startTime>=getMaxValueSeconds())
                break;

            DijkstraMethod(graph, queue, uniqueTravelInfo, travelInfo, currentTime, currentStop);

        }

    }

    @Override
    public LinkedList<TravelInformation> getShortestPath(String start, String end, StopGraph graph, int startTime) {
        return null;
    }
}

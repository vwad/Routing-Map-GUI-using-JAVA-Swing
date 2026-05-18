package Routing.Algorithm;

import Routing.Graph.*;

import java.util.*;

public class DijkstraRouting extends Dijkstra{

    @Override
    public Map<String,TravelInformation> generatePaths(String start, String end, StopGraph graph,int startTime, Set<String> closure) {
        TravelInformation startInfo = new WalkingTravelInformation(0,graph.getStop(start), graph.getStop(start));
        startInfo.setDepartureTime(startTime);

        return generatePaths(startInfo,graph,start, this);
    }

    @Override
    public LinkedList<TravelInformation> getShortestPath(String start, String end, StopGraph graph,int startTime) {
        Map<String,TravelInformation> paths = generatePaths(start,end,graph,startTime, null);

        LinkedList<TravelInformation> pathToEnd = new LinkedList<>();
        String currentNode = end;

        while (!currentNode.equals(start))
        {

            TravelInformation information = paths.get(currentNode);
            pathToEnd.addFirst(information);
            currentNode=information.getDeparturePoint();

        }

        return pathToEnd;
    }

    public void generateAllPaths(TravelInformation startInfo, PriorityQueue<TravelInformation> queue, StopGraph graph, Map<String, TravelInformation> uniqueTravelInfo)
    {

        while (!queue.isEmpty()) {

            TravelInformation travelInfo = queue.poll();
            double currentTime = travelInfo.getArrivalTime();
            String currentStop = travelInfo.getArrivalPoint();

            if(visited.contains(currentStop))
                continue;

            if(currentStop.equals("end"))
                break;

            DijkstraMethod(graph, queue, uniqueTravelInfo, travelInfo, currentTime, currentStop);

        }
    }

}

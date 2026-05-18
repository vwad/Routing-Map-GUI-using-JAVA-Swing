package Routing.Algorithm;

import Routing.Graph.StopGraph;
import Routing.Graph.Travel;
import Routing.Graph.TravelInformation;
import Routing.Graph.WalkingTravelInformation;
import java.util.*;


public abstract class Dijkstra implements ShortestPathAlgorithm{
    protected Set<String> visited = new HashSet<>();
    protected Set<String> closures = new HashSet<>();

    public void addVisited(Set<String> visited)
    {
        this.visited = visited;
    }

    public void addClosures(Set<String> closures)
    {
        this.closures = closures;
    }

    public Map<String,TravelInformation> generatePaths(TravelInformation startInfo,StopGraph graph,String start, Dijkstra dijkstraInstance)
    {
        //queue will contain all the nodes in the queue sorted by weight
        PriorityQueue<TravelInformation> queue = new PriorityQueue<>(
                Comparator.comparing(node -> node.getArrivalTime())
        );
        queue.add(startInfo);

        Map<String,TravelInformation> uniqueTravelInfo = new HashMap<>();
        uniqueTravelInfo.put(start,startInfo);

        dijkstraInstance.generateAllPaths(startInfo,queue,graph,uniqueTravelInfo);

        return uniqueTravelInfo;
    }

    protected void DijkstraMethod(StopGraph graph, PriorityQueue<TravelInformation> queue,
                                Map<String, TravelInformation> uniqueTravelInfo, TravelInformation travelInfo, double currentTime, String currentStop) {
        Map<String, Travel> neighbours = graph.getNeighbors(currentStop);

        if(neighbours!=null)
        {

            for (Map.Entry<String,Travel> entry : neighbours.entrySet()) {

                if (entry.getValue() == null)
                    continue;

                TravelInformation nextTravelInfo = entry.getValue().getTravelInfoAfterTime(currentTime);

                if (nextTravelInfo == null)
                    continue;

                String nextStop = nextTravelInfo.getArrivalPoint();

                if (!uniqueTravelInfo.containsKey(nextStop)) {

                    TravelInformation holder = new WalkingTravelInformation(0, graph.getStop(currentStop), graph.getStop(nextStop));
                    holder.setDepartureTime(Integer.MAX_VALUE);
                    uniqueTravelInfo.put(nextStop, holder);
                }

                if (uniqueTravelInfo.get(nextStop).getArrivalTime() > nextTravelInfo.getArrivalTime()) {

                    if(uniqueTravelInfo.get(currentStop).getDepartureNode().getId().equals(nextStop))
                        continue;

                    if((closures.contains(nextStop)&&!travelInfo.getTrip_id().equals(nextTravelInfo.getTrip_id()))
                            || (closures.contains(currentStop)&&!travelInfo.getTrip_id().equals(nextTravelInfo.getTrip_id())))
                        continue;

                    uniqueTravelInfo.put(nextStop, nextTravelInfo);
                    queue.add(nextTravelInfo);

                }

            }

        }

        visited.add(currentStop);
    }
}

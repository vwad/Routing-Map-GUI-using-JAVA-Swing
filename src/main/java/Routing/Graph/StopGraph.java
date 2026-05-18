package Routing.Graph;

import java.util.*;
import static Routing.Graph.GraphFactory.WALKING_DISTANCE_KM;

public class StopGraph {
    private Map<String,Map<String,Travel>> graphMatrix;
    private Map<String , Integer> stopMap = new HashMap<>();
    private List<Node> stops;
    public StopGraph(List<Node> nodes)
    {
        this.stops = nodes;
        graphMatrix = new HashMap<>(nodes.size());
        addNodes(nodes);
    }

    private void addNodes(List<Node> nodes)
    {
        int counter = 0;
        for(Node node : nodes)
        {
            graphMatrix.put(node.getId(),new HashMap<>());
            stopMap.put(node.getId(),counter++);
        }
    }

    public void replaceNode(String name, Node node)
    {
        stops.set(stopMap.get(name),node);
    }

    public Set<String> getStops()
    {   Set<String> stops = new HashSet<>(stopMap.keySet());
        stops.remove("end");
        stops.remove("start");
        return stops;
    }

    public void addTravel(String from, String to, TravelInformation travelInformation)
    {
        Travel travel = getTravel(from,to);
        if(travel == null)
            return;
        travel = graphMatrix.get(from).get(to);
        travel.appendTravel(travelInformation);
        //if null, create travel and append, if not, just get and append
    }

    public Travel getTravel(String from, String to)
    {
        if(graphMatrix.get(from)==null || graphMatrix.get(to)==null)
            return null;
        Travel travel;
        if(graphMatrix.get(from).get(to)==null)
        {
            travel = new Travel();
            graphMatrix.get(from).put(to,travel);
        }
        travel = graphMatrix.get(from).get(to);
        return travel;

    }

    public void addWalk(String from, String to, TravelInformation travelInformation)
    {
        Travel travel = getTravel(from,to);
        if(travel == null)
            return;
        travel = graphMatrix.get(from).get(to);
        travel.setWalk(travelInformation);
    }

    public void changeStartEnd(double fromLat, double fromLon, double toLat, double toLon)
    {
        resetTravelsFrom("start");
        resetTravelsTo("end");
        Node start = new StopNode("start","start",fromLat,fromLon);
        Node end = new StopNode("end","end",toLat,toLon);
        replaceNode("start",start);
        replaceNode("end",end);
        WalkBuilder walkBuilder = new WalkBuilder();
        walkBuilder.addWalkFromStartToEnd(this,WALKING_DISTANCE_KM,stops);
        walkBuilder.addStartEndWalk(this,fromLat,fromLon,toLat,toLon);
    }

    public Node getStop(String id) {
        if(stopMap.get(id)==null)
            return null;
        return stops.get(stopMap.get(id));
    }

    public void resetTravelsTo(String to)
    {
        for(Map.Entry<String,Map<String,Travel>> entry : graphMatrix.entrySet())
        {
            if(entry.getValue().get(to)!=null)
                entry.getValue().put(to,null);
        }
    }

    public void resetTravelsFrom(String from)
    {
        if(graphMatrix.get(from)!=null)
            graphMatrix.put(from,new HashMap<>());
    }

    public Map<String,Travel> getNeighbors(String from)
    {
        return graphMatrix.get(from);
    }

    public List<Node> getStopList(){
        List<Node> stops = new ArrayList<>(this.stops);
        stops.removeLast();
        stops.removeLast();
        return stops;
    }

    public String toString()
    {
        return graphMatrix.toString();
    }

    /**
     * gets nodes directed into
     * @param to node identifier in the graph
     * @return al the travel times coming from the node
     */
}

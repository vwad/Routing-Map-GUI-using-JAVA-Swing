package Routing.Graph;

import java.util.LinkedHashMap;
import java.util.Map;

public class WalkingTravelInformation implements TravelInformation {
    private double walkingTime;
    private double departureTime;
    private Node fromNode;
    private Node toNode;
    private String type = "walk";

    public WalkingTravelInformation(double walkingTime, Node fromNode, Node toNode)
    {
        this.walkingTime = walkingTime;
        this.departureTime = 0;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    @Override
    public Node getArrivalNode() {
        return toNode;
    }

    @Override
    public Node getDepartureNode() {
        return fromNode;
    }

    @Override
    public boolean isWalk() {
        return true;
    }

    @Override
    public double getDepartureTime() {
        return departureTime;
    }

    @Override
    public double getArrivalTime() {
        return departureTime + walkingTime;
    }

    @Override
    public double getWeight() {
        return walkingTime;
    }

    @Override
    public String getTrip_headsign() {
        return "";
    }

    @Override
    public String getRoute_long_name() {
        return "";
    }

    @Override
    public String getRoute_short_name() {
        return "";
    }

    @Override
    public String getAgency_name() {
        return "";
    }

    @Override
    public String getTrip_id() {
        return "";
    }

    @Override
    public void setDepartureTime(double departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public String getArrivalPoint() {
        return toNode.getId();
    }

    @Override
    public String getDeparturePoint(){
        return fromNode.getId();
    }

    @Override
    public String getType(){
        return type;
    }

    public String toString()
    {
        Map<String,Object> orderedMap = new LinkedHashMap<String,Object>();
        orderedMap.put("fromNode",fromNode);
        orderedMap.put("toNode",toNode);
        orderedMap.put("departure_time",departureTime);
        orderedMap.put("arrival_time",getArrivalTime());
        orderedMap.put("type","walking");
        return orderedMap.toString();
    }
}

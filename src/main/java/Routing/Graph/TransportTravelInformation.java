package Routing.Graph;

import java.util.LinkedHashMap;
import java.util.Map;

public class TransportTravelInformation implements TravelInformation {
    private double departureTime;
    private double arrivalTime;
    private Node fromNode;
    private Node toNode;
    private String agency_name;
    private String route_short_name;
    private String route_long_name;
    private String trip_headsign;
    private String trip_id;
    private String type = "transport";
    public TransportTravelInformation(double departureTime, double arrivalTime, Node fromNode, Node toNode,
                                      String agency_name, String route_short_name, String route_long_name, String trip_headsign, String trip_id)
    {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.agency_name = agency_name;
        this.route_short_name = route_short_name;
        this.route_long_name = route_long_name;
        this.trip_headsign = trip_headsign;
        this.trip_id = trip_id;
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
        return false;
    }

    @Override
    public double getDepartureTime() {
        return departureTime;
    }

    @Override
    public double getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public double getWeight() {
        return arrivalTime-departureTime;
    }

    public String toString()
    {
        Map<String,Object> orderedMap = new LinkedHashMap<String,Object>();
        orderedMap.put("fromNode",fromNode);
        orderedMap.put("toNode",toNode);
        orderedMap.put("departure_time",departureTime);
        orderedMap.put("arrival_time",getArrivalTime());
        orderedMap.put("type","transport");
        orderedMap.put("agency_name",agency_name);
        orderedMap.put("route_short_name",route_short_name);
        orderedMap.put("route_long_name",route_long_name);
        orderedMap.put("trip_headsign",trip_headsign);
        return orderedMap.toString();
    }
    public String getTrip_headsign()
    {
        return trip_headsign;
    }

    @Override
    public String getRoute_long_name() {
        return route_long_name;
    }

    @Override
    public String getRoute_short_name() {
        return route_short_name;
    }

    @Override
    public String getAgency_name() {
        return agency_name;
    }

    @Override
    public String getTrip_id() {
        return trip_id;
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

}

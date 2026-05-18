package Routing.Graph;

public interface TravelInformation {
    String getArrivalPoint();
    String getDeparturePoint();
    Node getArrivalNode();
    Node getDepartureNode();
    boolean isWalk();
    void setDepartureTime(double departureTime);
    double getWeight();
    double getDepartureTime();
    double getArrivalTime();
    String getTrip_headsign();
    String getRoute_long_name();
    String getRoute_short_name();
    String getAgency_name();
    String getTrip_id();
    String getType();
}

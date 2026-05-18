package Routing.Graph;

import org.jxmapviewer.viewer.GeoPosition;

public class StopNode implements Node {
    private String id;
    private String name;
    private double lat;
    private double lon;

    public StopNode(String id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "[id=" + id + ", name=" + name + ", lat=" + lat + ", lon=" + lon+"]";
    }
}

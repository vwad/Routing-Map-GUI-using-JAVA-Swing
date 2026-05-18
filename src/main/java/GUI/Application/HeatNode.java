package GUI.Application;

import Routing.Graph.Node;

public class HeatNode implements Node {
    private double value;
    private double lat;
    private double lon;
    private int id;

    public HeatNode(double lat, double lon, int id)
    {
        this.lat = lat;
        this.lon = lon;
        this.id = id;
    }

    public void setValue(double value)
    {
        this.value = Math.max(this.value,value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getId() {
        return "Heat-"+id;
    }

    @Override
    public double getLat() {
        return lat;
    }

    public void resetValue()
    {
        this.value = 0;
    }

    @Override
    public double getLon() {
        return lon;
    }

    @Override
    public String getName() {
        return "";
    }
}

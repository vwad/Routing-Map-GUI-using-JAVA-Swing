package Closure.Distributed;

public class PopulationInformation {
    private double minLat;
    private double minLon;
    private double maxLat;
    private double maxLon;
    private double percentage;
    public PopulationInformation(double minLat, double minLon, double maxLat, double maxLon, double percentage)
    {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
        this.percentage = percentage;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMaxLon() {
        return maxLon;
    }


    public double getPercentage() {
        return percentage;
    }

}

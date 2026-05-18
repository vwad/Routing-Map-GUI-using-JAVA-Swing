package Closure;

public class ClosureInfo {
    
    private Trip trip;
    private String startTime, date;
    private double travelTime;
    private int tripID;


    public ClosureInfo(Trip trip, String startTime, String date, double travelTime,int tripID)
    {
        this.trip = trip;
        this.startTime = startTime;
        this.date = date;
        this.travelTime = travelTime;
        this.tripID = tripID;
    }

    public Trip getTrip()
    {
        return trip;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public String getDate()
    {
        return date;
    }

    public double getTravelTime()
    {
        return travelTime;
    }

    public int getTripID()
    {
        return tripID;
    }


}

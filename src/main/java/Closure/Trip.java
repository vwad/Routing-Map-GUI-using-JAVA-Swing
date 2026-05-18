package Closure;

public class Trip {
    
    private double latA, lonA, latB, lonB;

    public Trip(double latA, double lonA, double latB, double lonB){
        this.latA = latA;
        this.lonA = lonA;
        this.latB = latB;
        this.lonB = lonB;
    }

    public double getLatA(){
        return latA;
    }

    public double getLonA(){
        return lonA;
    }

    public double getLatB(){
        return latB;
    }

    public double getLonB(){
        return lonB;
    }
}

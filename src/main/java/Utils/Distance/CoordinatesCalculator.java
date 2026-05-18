package Utils.Distance;

import JSON.JSON;

public class CoordinatesCalculator {
    private static final double R = 6371; //earth radius
    private static final double C = 2 * Math.PI * R; //Circumference of earth

    public static double getArea(double[] leftUpper, double[] rightGround)
    {
        double dy = (leftUpper[0]-rightGround[0]);
        double dx = (rightGround[1]-leftUpper[1]);
        double degreeSize = C/360;
        double centerX = (leftUpper[0]+rightGround[0])/2;
        double height = degreeSize * dy;
        double width = degreeSize * Math.cos(Math.toRadians(centerX)) * dx;
        return (width*height);
    }

    public static double[] getDistanceLatLon(double length, double[] center) {
        double Y = center[0];
        double dY = length / (C / 360); //Get difference by which latitude will shift on y
        double dX = (dY / Math.cos(Math.toRadians(Y))); //Using it and property of the longitude we calculate shift on x
        return new double[]{dX,dY};
    }

    public static double calculateDistanceKMForJSONS(JSON start, JSON end) {
        double lat1 = (double) start.get("lat");
        double lon1 = (double) start.get("lon");
        double lat2 = (double) end.get("lat");
        double lon2 = (double) end.get("lon");
        return calculateDistanceKM(lat1,lon1,lat2,lon2);
    }

    public static double calculateDistanceKM(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double latSin = Math.sin(dLat / 2);
        double lonSin = Math.sin(dLon / 2);
        double a = Math.pow(latSin,2) + Math.cos(radLat1)*Math.cos(radLat2) * Math.pow(lonSin,2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }



    public static void main(String[] args)
    {
        System.out.println(((calculateDistanceKM(49.598575,6.126667,49.600514,6.136658)/5)*60));

    }

}

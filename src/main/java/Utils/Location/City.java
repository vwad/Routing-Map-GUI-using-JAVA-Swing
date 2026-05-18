package Utils.Location;

public abstract class City extends Location{
    public City(double[] rightUpperCornerCoordinates, double[] leftGroundCornerCoordinates, double[] centerCoordinates) {
        super(rightUpperCornerCoordinates, leftGroundCornerCoordinates, centerCoordinates);
    }
}

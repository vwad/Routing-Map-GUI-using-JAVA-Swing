package Utils.Location;

public abstract class Country extends Location{
    public Country(double[] rightUpperCornerCoordinates, double[] leftGroundCornerCoordinates, double[] centerCoordinates) {
        super(rightUpperCornerCoordinates, leftGroundCornerCoordinates, centerCoordinates);
    }
}

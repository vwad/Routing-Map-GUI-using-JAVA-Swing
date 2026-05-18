package Utils.Location;

public abstract class Location {
    private double[] rightUpperCornerCoordinates;
    private double[] leftGroundCornerCoordinates;
    private double[] centerCoordinates;
    public Location(double[] rightUpperCornerCoordinates, double[] leftGroundCornerCoordinates, double[] centerCoordinates)
    {
        this.rightUpperCornerCoordinates = rightUpperCornerCoordinates;
        this.leftGroundCornerCoordinates = leftGroundCornerCoordinates;
        this.centerCoordinates = centerCoordinates;
    }

    public double[] getLeftGroundCornerCoordinates()
    {
        return leftGroundCornerCoordinates;
    }
    public double[] getRightUpperCornerCoordinates()
    {
        return rightUpperCornerCoordinates;
    }

    public double[] getRightGroundCornerCoordinates()
    {
        return new double[]{leftGroundCornerCoordinates[0],rightUpperCornerCoordinates[1]};
    }

    public double[] getLeftUpperCornerCoordinates()
    {
        return new double[]{rightUpperCornerCoordinates[0],leftGroundCornerCoordinates[1]};
    }

    public double[] getCenterCoordinates()
    {
        return centerCoordinates;
    }

    public void setLeftGroundCornerCoordinates(double[] leftGroundCornerCoordinates) {
        this.leftGroundCornerCoordinates = leftGroundCornerCoordinates;
    }

    public void setCenterCoordinates(double[] centerCoordinates) {
        this.centerCoordinates = centerCoordinates;
    }

    public void setRightUpperCornerCoordinates(double[] rightUpperCornerCoordinates) {
        this.rightUpperCornerCoordinates = rightUpperCornerCoordinates;
    }
}

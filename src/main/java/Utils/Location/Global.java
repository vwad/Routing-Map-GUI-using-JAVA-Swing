package Utils.Location;

public class Global extends Location {
    private static final double[] RIGHT_UPPER_CORNER_COORDINATES = new double[]{180.0, 90.0};
    private static final double[] LEFT_GROUND_CORNER_COORDINATES = new double[]{-180.0, -90.0};
    private static final double[] CENTER_COORDINATES = new double[]{0.0, 0.0};
    public Global() {
        super(RIGHT_UPPER_CORNER_COORDINATES, LEFT_GROUND_CORNER_COORDINATES, CENTER_COORDINATES);
    }
}

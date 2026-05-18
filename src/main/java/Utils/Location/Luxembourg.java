package Utils.Location;

public class Luxembourg extends Country{
    private static final double[] RIGHT_UPPER_CORNER_COORDINATES = new double[]{50.1280516628, 6.528};
    private static final double[] LEFT_GROUND_CORNER_COORDINATES = new double[]{49.4426671413, 5.67405195478};
    private static final double[] CENTER_COORDINATES = new double[]{49.609367,6.129371};
    public Luxembourg()
    {
        super(RIGHT_UPPER_CORNER_COORDINATES,LEFT_GROUND_CORNER_COORDINATES,CENTER_COORDINATES);
    }
}

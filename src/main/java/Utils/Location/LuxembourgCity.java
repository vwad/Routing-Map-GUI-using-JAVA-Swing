package Utils.Location;

public class LuxembourgCity extends City {
    //TODO: ADD COORDINATES HERE
    private static final double[] RIGHT_UPPER_CORNER_COORDINATES = new double[]{49.66785002766794, 6.326408386230469};
    private static final double[] LEFT_GROUND_CORNER_COORDINATES = new double[]{49.553948238365486,5.976047515869141};
    private static final double[] CENTER_COORDINATES = new double[]{49.609367,6.129371};
    public LuxembourgCity()
    {
        super(RIGHT_UPPER_CORNER_COORDINATES,LEFT_GROUND_CORNER_COORDINATES,CENTER_COORDINATES);
    }
}

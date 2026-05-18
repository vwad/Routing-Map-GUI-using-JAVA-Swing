package Utils.Time;

import java.util.Scanner;

public class WalkTimeCalculator {
    private static final int WALKING_SPEED_KM = 5;
    private static final double HOURS_TO_SECONDS_RATIO = 3600;

    public static double distanceToWalkingSeconds(double calculatedDistanceKM)
    {
        return (calculatedDistanceKM / WALKING_SPEED_KM) * HOURS_TO_SECONDS_RATIO;
    }

    public static boolean timeIsValid(String time)
    {
        String[] parts = time.split(":");

        if(parts.length>3)
            return false;

        Scanner scanner;

        for(String part : parts)
        {
            scanner = new Scanner(part);

            if(!scanner.hasNextInt())

                return false;

            if(part.length()!=2)

                return false;

        }
        return true;
    }
}

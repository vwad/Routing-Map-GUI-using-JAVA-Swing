package Closure.Basic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import Utils.Location.Location;
import Utils.Location.LuxembourgCity;

public class CreateTrips {
    
    public CreateTrips(Location city, int amountOfTrips) throws IOException
    {
        double[] rightBound = city.getRightUpperCornerCoordinates();
        double[] leftBound = city.getLeftGroundCornerCoordinates();
        File file = new File("data/Closures.txt");
        FileWriter writer = new FileWriter(file, false);
        BufferedWriter bof = new BufferedWriter(writer);
        Random random = new Random();
        for(int i = 0; i < amountOfTrips;i++)
        {
            double latA = random.nextDouble(leftBound[0], rightBound[0]);
            double lonA = random.nextDouble(leftBound[1], rightBound[1]);
            
            double latB = random.nextDouble(leftBound[0], rightBound[0]);
            double lonB = random.nextDouble(leftBound[1], rightBound[1]);

            bof.append(latA + " , " + lonA + " , " + latB + " , " + lonB);
            bof.newLine();
            
        }
        bof.close();
    }

    public static void main(String[] args) throws IOException {
        new CreateTrips(new LuxembourgCity(), 1000);
    }
}

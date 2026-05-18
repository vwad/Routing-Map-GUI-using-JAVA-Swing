package Closure.Distributed;

import SQLDatabase.Config.SQLiteBaseConfigFactory;
import SQLDatabase.Database.Connections;
import SQLDatabase.Database.CurrentSQLiteDatabase;
import Utils.Distance.CoordinatesCalculator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DistributedCreateTrips {

    private static final int GRID_SIZE = 1; //km

    public DistributedCreateTrips(Connection c, int amountOfTrips) throws IOException, SQLException {
        List<PopulationInformation> informationList = new ArrayList<>();
        Statement statement = c.createStatement();
        ResultSet result = statement.executeQuery("SELECT *, population/(select sum(population) from POPULATION_PER_KM) as percentage from POPULATION_PER_KM order by population asc");
        double previous = 0;

        while (result.next())
        {
            double minLat = result.getDouble("lat");
            double minLon = result.getDouble("lon");
            double[] dxAndDy = CoordinatesCalculator.getDistanceLatLon(GRID_SIZE,new double[]{minLat,minLon});
            double maxLat = minLat+dxAndDy[1];
            double maxLon = minLon+dxAndDy[0];
            double percentage =  result.getDouble("percentage");

            percentage+=previous;

            previous = percentage;

            PopulationInformation information = new PopulationInformation(minLat,minLon,maxLat,maxLon,percentage);
            informationList.add(information);
        }

        PopulationInformation information;
        File file = new File("data/DistributedClosures.txt");
        FileWriter writer = new FileWriter(file, false);
        BufferedWriter bof = new BufferedWriter(writer);
        Random random = new Random();
        for(int i = 0; i < amountOfTrips;i++)
        {
            information = getRandomPopulationLocation(random,informationList);

            double latA = random.nextDouble(information.getMinLat(), information.getMaxLat());
            double lonA = random.nextDouble(information.getMinLon(), information.getMaxLon());

            information = getRandomPopulationLocation(random,informationList);

            double latB = random.nextDouble(information.getMinLat(), information.getMaxLat());
            double lonB = random.nextDouble(information.getMinLon(), information.getMaxLon());

            bof.append(latA + " , " + lonA + " , " + latB + " , " + lonB);
            bof.newLine();

        }
        bof.close();
    }

    public PopulationInformation getRandomPopulationLocation(Random random, List<PopulationInformation> informationList)
    {
        double chance = random.nextDouble(1);
        for(PopulationInformation information : informationList)
        {
            if(chance<information.getPercentage())
                return information;
        }
        if(!informationList.isEmpty())
            return informationList.getLast();
        return null;
    }

    public static void main(String[] args) throws IOException, SQLException {
        CurrentSQLiteDatabase.setCurrentConnection(Connections.JDBC_DATABASE_CONNECTION,new SQLiteBaseConfigFactory().getConfig());
        Connection c = CurrentSQLiteDatabase.getCurrentConnection();
        new DistributedCreateTrips(c, 6000);
    }
}

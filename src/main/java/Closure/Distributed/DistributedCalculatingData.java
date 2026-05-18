package Closure.Distributed;

import Closure.Trip;
import Routing.Engine.DijkstraStarter;
import Routing.Graph.FullDayGraphFactory;
import Routing.Graph.StopGraph;
import Routing.Graph.TravelInformation;
import SQLDatabase.Config.SQLiteBaseConfigFactory;
import SQLDatabase.Database.Connections;
import SQLDatabase.Database.CurrentSQLiteDatabase;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import SQLDatabase.Query.QueryExecutor;
import Utils.Location.Location;
import Utils.Location.LuxembourgCity;
import Utils.Time.DateCalculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DistributedCalculatingData {
    public static String[] days = {"2025-05-26", "2025-05-27", "2025-05-28", "2025-05-29", "2025-05-30", "2025-05-31", "2025-06-01"};

    public DistributedCalculatingData(Location city, Connection c) throws IOException, DatabaseNotLoadedException, SQLException
    {
        c.setAutoCommit(false);
        Statement statement = c.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS DISTRIBUTEDCLOSURE");
        statement.executeUpdate("CREATE TABLE DISTRIBUTEDCLOSURE (latA double, lonA double, latB double, lonB double, startTime TIME, startDate DATE, travelTime double, tripID integer PRIMARY KEY)");
        PreparedStatement prepStatement = c.prepareStatement("INSERT INTO DISTRIBUTEDCLOSURE VALUES (?,?,?,?,?,?,?,?)");

        List<Trip> trips = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("data\\DistributedClosures.txt"));
        String line;

        while((line = br.readLine()) != null)
        {
            String [] parts = line.split(" , ");
            double latA = Double.valueOf(parts[0]);
            double lonA = Double.valueOf(parts[1]);
            double latB = Double.valueOf(parts[2]);
            double lonB = Double.valueOf(parts[3]);
            Trip obj = new Trip(latA, lonA, latB, lonB);
            trips.add(obj);
        }
        int counter = 0;
        for(String day : days)
        {
            FullDayGraphFactory graph = new FullDayGraphFactory();
            Distribution timeDistribution = QueryExecutor.buildDataDistribution(c,city,day,0,24*3600);
            Trip obj = trips.get(0);
            StopGraph gr = graph.getGraph(city, day, "00:00:00", obj.getLatA(), obj.getLonA(), obj.getLatB(), obj.getLonB());
            for(Trip trip : trips)
            {
                DijkstraStarter dijkstra = new DijkstraStarter();
                String time = DateCalculator.formatTimeSecToString(timeDistribution.getRandom());
                gr.changeStartEnd(trip.getLatA(),trip.getLonA(),trip.getLatB(),trip.getLonB());
                LinkedList<TravelInformation> list = dijkstra.getRouteFromTo(gr, time);
                double travelTime = list.getLast().getArrivalTime()-list.getFirst().getDepartureTime();
                prepStatement.setDouble(1, trip.getLatA());
                prepStatement.setDouble(2, trip.getLonA());
                prepStatement.setDouble(3, trip.getLatB());
                prepStatement.setDouble(4, trip.getLonB());
                prepStatement.setString(5, time);
                prepStatement.setString(6, day);
                prepStatement.setDouble(7, travelTime);
                prepStatement.setInt(8, counter);
                prepStatement.addBatch();
                counter++;
                prepStatement.executeBatch();
            }
        }
        c.commit();
        c.setAutoCommit(true);
    }

    public static void main(String[] args){

        try {
            CurrentSQLiteDatabase.setCurrentConnection(Connections.JDBC_DATABASE_CONNECTION,new SQLiteBaseConfigFactory().getConfig());
            Connection c = CurrentSQLiteDatabase.getCurrentConnection();
            new DistributedCalculatingData(new LuxembourgCity(), c);
        } catch (IOException | DatabaseNotLoadedException | SQLException e) {
            e.printStackTrace();
        }
    }

}

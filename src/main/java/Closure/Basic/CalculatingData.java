package Closure.Basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Closure.Trip;
import Routing.Engine.DijkstraStarter;
import Routing.Graph.FullDayGraphFactory;
import Routing.Graph.StopGraph;
import Routing.Graph.TravelInformation;
import SQLDatabase.Config.SQLiteBaseConfigFactory;
import SQLDatabase.Database.Connections;
import SQLDatabase.Database.CurrentSQLiteDatabase;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import Utils.Location.Location;
import Utils.Location.LuxembourgCity;

public class CalculatingData {
    
    public static String[] days = {"2025-05-26", "2025-05-27", "2025-05-28", "2025-05-29", "2025-05-30", "2025-05-31", "2025-06-01"};
    public static String[] time = {"06:00:00", "09:00:00" , "12:00:00" , "15:00:00" , "18:00:00" , "21:00:00"};
    
    public CalculatingData(Location city, Connection c) throws IOException, DatabaseNotLoadedException, SQLException
    {
        c.setAutoCommit(false);
        Statement statement = c.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS CLOSURE");
        statement.executeUpdate("CREATE TABLE CLOSURE (latA double, lonA double, latB double, lonB double, startTime TIME, startDate DATE, travelTime double, tripID integer PRIMARY KEY)");
        PreparedStatement prepStatement = c.prepareStatement("INSERT INTO CLOSURE VALUES (?,?,?,?,?,?,?,?)");
        
        
        List<Trip> trips = new ArrayList<>();
        
        BufferedReader br = new BufferedReader(new FileReader(new File("data/closures/Closures.txt")));
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
            Trip obj = trips.get(0);
            StopGraph gr = graph.getGraph(city, day, "06:00:00", obj.getLatA(), obj.getLonA(), obj.getLatB(), obj.getLonB());

            for(Trip trip : trips)
            {
                for(String times : time)
                {
                    gr.changeStartEnd(trip.getLatA(), trip.getLonA(), trip.getLatB(), trip.getLonB());
                    DijkstraStarter dijkstra = new DijkstraStarter();
                    LinkedList<TravelInformation> list = dijkstra.getRouteFromTo(gr, times);
                    double travelTime = list.getLast().getArrivalTime()-list.getFirst().getDepartureTime();
                    prepStatement.setDouble(1, trip.getLatA());
                    prepStatement.setDouble(2, trip.getLonA());
                    prepStatement.setDouble(3, trip.getLatB());
                    prepStatement.setDouble(4, trip.getLonB());
                    prepStatement.setString(5, times);
                    prepStatement.setString(6, day);
                    prepStatement.setDouble(7, travelTime);
                    prepStatement.setInt(8, counter);
                    prepStatement.addBatch();
                    counter++;
                }
                prepStatement.executeBatch();
            }
        }
        c.commit();
        c.setAutoCommit(true);
    }

    public static void main(String[] args) throws IOException, DatabaseNotLoadedException, SQLException {
        CurrentSQLiteDatabase.setCurrentConnection(Connections.JDBC_DATABASE_CONNECTION,new SQLiteBaseConfigFactory().getConfig());
        Connection c = CurrentSQLiteDatabase.getCurrentConnection();
        
        new CalculatingData(new LuxembourgCity(), c);
    }
}

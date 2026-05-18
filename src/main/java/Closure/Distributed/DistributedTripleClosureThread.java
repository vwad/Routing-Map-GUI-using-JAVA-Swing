package Closure.Distributed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import Closure.Basic.TripleClosureThread;
import Closure.ClosureInfo;
import Closure.Trip;
import Routing.Engine.ClosuresDijkstraStarter;
import Routing.Graph.FullDayGraphFactory;
import Routing.Graph.StopGraph;
import Routing.Graph.TravelInformation;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import Utils.Location.Location;

public class DistributedTripleClosureThread implements Runnable{
    private Location city;
    private PreparedStatement prepStatement;
    private List<ClosureInfo> info;
    private static int operationCount;
    public static long startTime;
    private Connection c;
    public static Set<Set<String>> combinations;

    protected DistributedTripleClosureThread(Connection c, Location city, List<ClosureInfo> info, int start, int end) throws SQLException {
        Statement statement = c.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS DistributedRemovedThreeStops");
        statement.executeUpdate("CREATE TABLE DistributedRemovedThreeStops ( " +
                "removedStop1 text not null , " +
                "removedStop2 text not null , " +
                "removedStop3 text not null , " +
                "lostTime double not null , " +
                "tripID integer not null , " +
                "Foreign key (tripID) references DISTRIBUTEDCLOSURE(tripID), " +
                "foreign key (removedStop1) references Stops (stop_id), " +
                "foreign key (removedStop2) references Stops (stop_id), " +
                "foreign key (removedStop3) references Stops (stop_id) " +
                ")");

        this.city = city;
        this.prepStatement = c.prepareStatement("INSERT INTO DistributedRemovedThreeStops VALUES (?,?,?,?,?)");;
        this.info = info.subList(start,end);
        this.c = c;
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            System.out.println("Trip count: " + info.size());
            removeStops(city,prepStatement,info);
            System.out.println("Time to load: " + (System.currentTimeMillis()-start));
        } catch (SQLException | DatabaseNotLoadedException e) {
            e.printStackTrace();
        }
    }

    public void getStopClosures() throws SQLException {
        Set<Set<String>> combinations = new HashSet<>();
        List<String> stops = new ArrayList<>();
        Statement statement = c.createStatement();
        ResultSet resultSet = statement.executeQuery("select sum(lostTime), removedStop " +
                "from DistributedRemovedStops " +
                "group by removedStop " +
                "having sum(lostTime) != 0 " +
                "order by sum(lostTime) desc " +
                "limit 20;");
        while(resultSet.next())
        {
            stops.add(resultSet.getString(2));
        }

        for(int i = 0; i < stops.size();i++)
        {
            String stop1 = stops.get(i);
            for(int j = i+1; j < stops.size(); j++)
            {
                String stop2 = stops.get(j);
                for(int k = j + 1; k < stops.size(); k++)
                {
                    String stop3 = stops.get(k);
                    Set<String> combination = new HashSet<>();
                    combination.add(stop1);
                    combination.add(stop2);
                    combination.add(stop3);
                    combinations.add(combination);
                }
            }
        }
        DistributedTripleClosureThread.combinations = combinations;
    }

    private void removeStops(Location city, PreparedStatement prepStatement, List<ClosureInfo> info) throws DatabaseNotLoadedException, SQLException {
        FullDayGraphFactory graph = new FullDayGraphFactory();
        String date = "";
        StopGraph stopGraph = null;
        Set<Set<String>> stops = combinations;
        String stop1, stop2, stop3;

        ClosuresDijkstraStarter dijkstra = new ClosuresDijkstraStarter();
        for (ClosureInfo information : info) {
            Trip trip = information.getTrip();
            if (!date.equals(information.getDate())) {
                stopGraph = graph.getGraph(city, information.getDate(), "00:00:00", trip.getLatA(), trip.getLonA(), trip.getLatB(), trip.getLonB());
                date = information.getDate();

            }
            stopGraph.changeStartEnd(trip.getLatA(), trip.getLonA(), trip.getLatB(), trip.getLonB());
            for (Set<String> stop : stops) {
                if(++operationCount%1000000==0)
                {
                    System.out.println("Time to load 1 million operations: " + (System.currentTimeMillis()-startTime));
                    startTime = System.currentTimeMillis();
                }
                System.out.println(operationCount);
                List<String> stopList = stop.stream().toList();
                stop1 = stopList.get(0);
                stop2 = stopList.get(1);
                stop3 = stopList.get(2);

                LinkedList<TravelInformation> list = dijkstra.getRouteFromToWithClosures(stopGraph, information.getStartTime(), stop);
                double travelTime = list.getLast().getArrivalTime() - list.getFirst().getDepartureTime();
                prepStatement.setString(1, stop1);
                prepStatement.setString(2, stop2);
                prepStatement.setString(3, stop3);
                prepStatement.setDouble(4, information.getTravelTime() - travelTime);
                prepStatement.setInt(5, information.getTripID());
                prepStatement.addBatch();
            }

            prepStatement.executeBatch();
        }
    }

    public static void main(String[] args) throws SQLException {

    }
}

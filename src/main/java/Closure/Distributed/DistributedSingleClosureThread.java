
    package Closure.Distributed;

import Closure.ClosureInfo;
import Closure.Trip;
import Routing.Engine.ClosuresDijkstraStarter;
import Routing.Graph.FullDayGraphFactory;
import Routing.Graph.StopGraph;
import Routing.Graph.TravelInformation;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import Utils.Location.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DistributedSingleClosureThread implements Runnable{
    private Location city;
    private PreparedStatement prepStatement;
    private List<ClosureInfo> info;
    private static int operationCount;
    private static final int BATCH_SIZE = 500;
    public static long startTime;

    protected DistributedSingleClosureThread(Connection c, Location city, List<ClosureInfo> info, int start, int end) throws SQLException {
        Statement statement = c.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS DistributedRemovedStops");
        statement.executeUpdate("CREATE TABLE DistributedRemovedStops (removedStop text not null , lostTime double not null , tripID integer not null , Foreign key (tripID) references Closure(tripID), foreign key (removedStop) references Stops (stop_id))");

        this.city = city;
        this.prepStatement = c.prepareStatement("INSERT INTO DistributedRemovedStops VALUES (?,?,?)");;
        this.info = info.subList(start,end);
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

    private void removeStops(Location city, PreparedStatement prepStatement, List<ClosureInfo> info) throws DatabaseNotLoadedException, SQLException {
        FullDayGraphFactory graph = new FullDayGraphFactory();
        String date = "";
        StopGraph stopGraph = null;
        List<String> stops = null;

        ClosuresDijkstraStarter dijkstra = new ClosuresDijkstraStarter();
        for (ClosureInfo information : info) {
            Trip trip = information.getTrip();
            if (!date.equals(information.getDate())) {
                stopGraph = graph.getGraph(city, information.getDate(), "00:00:00", trip.getLatA(), trip.getLonA(), trip.getLatB(), trip.getLonB());
                date = information.getDate();
                if (stops == null) {
                    stops = stopGraph.getStops().stream().toList();
                }

            }
            stopGraph.changeStartEnd(trip.getLatA(), trip.getLonA(), trip.getLatB(), trip.getLonB());
            for (int i = 0; i < stops.size(); i++) {
                if(++operationCount%1000000==0)
                {
                    System.out.println("Time to load 1 million operations: " + (System.currentTimeMillis()-startTime));
                    startTime = System.currentTimeMillis();
                }
                System.out.println(operationCount);
                String stop = stops.get(i);
                Set<String> set = new HashSet<String>();
                set.add(stop);
                LinkedList<TravelInformation> list = dijkstra.getRouteFromToWithClosures(stopGraph, information.getStartTime(), set);
                double travelTime = list.getLast().getArrivalTime() - list.getFirst().getDepartureTime();
                prepStatement.setString(1, stop);
                prepStatement.setDouble(2, information.getTravelTime() - travelTime);
                prepStatement.setInt(3, information.getTripID());
                prepStatement.addBatch();
                if(operationCount%BATCH_SIZE==0)
                    prepStatement.executeBatch();
            }

            prepStatement.executeBatch();
        }
    }
}

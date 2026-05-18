package Routing.Graph;

import SQLDatabase.Database.CurrentSQLiteDatabase;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import Utils.Location.Location;
import Utils.Time.DateCalculator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FullDayGraphFactory extends GraphFactory{
    private static final String END_TIME = "24:00:00";

    @Override
    public StopGraph getGraph(Location city, String startDate, String startTime, double startLat, double startLon, double endLat, double endLon)  throws DatabaseNotLoadedException {
        try(Connection c = CurrentSQLiteDatabase.getCurrentConnection())
        {
            List<Node> stopList = executor.getNodes(c,city);
            StopGraph graph = getGraphForRoute(stopList,startLat,startLon,endLat,endLon);

            int startTimeSec = DateCalculator.formatTimeStringToSec(startTime);
            int endTimeSec = DateCalculator.formatTimeStringToSec(END_TIME);

            walkBuilder.addStartEndWalk(graph,startLat,startLon,endLat,endLon);
            initializeGraphRoute(c,stopList,city,startDate,endTimeSec,startTimeSec,graph);

            return graph;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public StopGraph getGraphForHeat(Location city, String startDate, String startTime, double startLat, double startLon) throws DatabaseNotLoadedException {
        try(Connection c = CurrentSQLiteDatabase.getCurrentConnection())
        {

            List<Node> stopList = executor.getNodes(c,city);
            StopGraph graph = getGraphForHeatMap(stopList,startLat,startLon);

            int startTimeSec = DateCalculator.formatTimeStringToSec(startTime);
            int endTimeSec = DateCalculator.formatTimeStringToSec(END_TIME);

            initializeGraphRoute(c,stopList,city,startDate,endTimeSec,startTimeSec,graph);

            return graph;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}

package Routing.Graph;

import SQLDatabase.Database.CurrentSQLiteDatabase;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import Utils.Location.Location;
import Utils.Time.DateCalculator;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TimeBoundedGraphFactory extends GraphFactory{
    @Override
    public StopGraph getGraph(Location city, String startDate, String startTime, double startLat, double startLon, double endLat, double endLon) throws DatabaseNotLoadedException {
        try(Connection c = CurrentSQLiteDatabase.getCurrentConnection())
        {
            List<Node> stopList = executor.getNodes(c,city);
            StopGraph graph = getGraphForRoute(stopList,startLat,startLon,endLat,endLon);

            int startEndTimeSec = (int) walkBuilder.addStartEndWalk(graph,startLat,startLon,endLat,endLon);
            int startTimeSec = DateCalculator.formatTimeStringToSec(startTime);
            int endTimeSec = startTimeSec+startEndTimeSec;

            initializeGraphRoute(c,stopList,city, startDate,endTimeSec,startTimeSec,graph);

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
            int endTimeSec = (int) (startTimeSec+HEAT_MAP_MAX_TIME);

            initializeGraphForHeat(c,stopList,city, startDate, endTimeSec,startTimeSec,graph);

            return graph;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}

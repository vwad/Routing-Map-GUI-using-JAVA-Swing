package Routing.Graph;

import GUI.Application.HeatMapCells;
import GUI.Application.HeatNode;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import SQLDatabase.Query.QueryExecutor;
import Utils.Distance.CoordinatesCalculator;
import Utils.Location.Location;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.util.List;

import static GUI.Application.HeatMapCells.*;

public abstract class GraphFactory {
    protected static final double WALKING_DISTANCE_KM = 1.5;
    protected static final double HEAT_MAP_MAX_TIME = getMaxValueSeconds();
    protected static final double HEAT_MAP_WALKING_DISTANCE_KM = getCellSize()*3;
    protected QueryExecutor executor = new QueryExecutor();
    protected WalkBuilder walkBuilder = new WalkBuilder();

    /**
     * A method that generates graph using information from the database, using at most needed to get graph for path generation
     * @param city city selected
     * @param startDate starting date
     * @param startTime starting time
     * @param startLat start point lat
     * @param startLon start point lon
     * @param endLat end point lat
     * @param endLon end point lon
     * @return a graph specified by the extended class
     * @throws DatabaseNotLoadedException if no stops exist within the database
     */
    abstract public StopGraph getGraph(Location city, String startDate, String startTime, double startLat, double startLon, double endLat, double endLon)throws DatabaseNotLoadedException;
    abstract public StopGraph getGraphForHeat(Location city, String startDate, String startTime, double startLat, double startLon)throws DatabaseNotLoadedException;

    private void initializeGraph(Connection c, List<Node> stopList, Location city,
                                 String startDate, int endTime, int startTime, StopGraph graph, double walkingDistance) throws SQLiteException
    {
        double[] dxAndDy = CoordinatesCalculator.getDistanceLatLon(walkingDistance,city.getCenterCoordinates());
        executor.addStopTravels(c,graph,city,startDate,startTime, endTime);
        walkBuilder.addWalkingTravels(dxAndDy,graph, walkingDistance,stopList);
    }

    protected void initializeGraphForHeat(Connection c, List<Node> stopList, Location city,
                                          String startDate, int endTime, int startTime, StopGraph graph) throws SQLiteException {
        initializeGraph(c,stopList,city,startDate,endTime,startTime,graph,GraphFactory.HEAT_MAP_WALKING_DISTANCE_KM);
    }

    protected void initializeGraphRoute(Connection c, List<Node> stopList, Location city,
                                        String startDate, int endTime, int startTime, StopGraph graph) throws SQLiteException {
        initializeGraph(c,stopList,city,startDate,endTime,startTime,graph,GraphFactory.WALKING_DISTANCE_KM);
    }

    protected StopGraph getGraphForHeatMap(List<Node> stopList, double startLat, double startLon) throws DatabaseNotLoadedException {
        if(stopList == null)
            throw new DatabaseNotLoadedException();
        Node start = new StopNode("start", "start", startLat, startLon);

        List<HeatNode> heatNodes = HeatMapCells.getMapCells();

        stopList.add(start);
        stopList.addAll(heatNodes);

        return new StopGraph(stopList);
    }

    protected StopGraph getGraphForRoute(List<Node> stopList, double startLat, double startLon, double endLat, double endLon) throws DatabaseNotLoadedException {
        if(stopList == null)
            throw new DatabaseNotLoadedException();
        Node start = new StopNode("start", "start", startLat, startLon);
        Node end = new StopNode("end", "end", endLat, endLon);

        stopList.add(start);
        stopList.add(end);

        return new StopGraph(stopList);
    }
}
package Routing.Engine;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import Routing.Algorithm.Dijkstra;
import Routing.Algorithm.DijkstraHeatMap;
import Routing.Algorithm.DijkstraRouting;
import Routing.Algorithm.ShortestPathAlgorithm;
import Routing.Graph.StopGraph;
import Routing.Graph.TravelInformation;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import Utils.Time.DateCalculator;

public class DijkstraStarter implements RoutingStarter {

    @Override
    public LinkedList<TravelInformation> getRouteFromTo(StopGraph graph, String startTime)
    {
        Dijkstra dijkstra = new DijkstraRouting();
        int startTimeSec = DateCalculator.formatTimeStringToSec(startTime);
        return dijkstra.getShortestPath("start","end",graph,startTimeSec);
    }

    @Override
    public Map<String, TravelInformation> getForHeatMap(StopGraph graph, String startTime, Set<String> closures)
    {
        Dijkstra dijkstra = new DijkstraHeatMap();
        int startTimeSec = DateCalculator.formatTimeStringToSec(startTime);
        return dijkstra.generatePaths("start",null,graph,startTimeSec, closures);
    }

    public static void main(String[] args) throws SQLException, DatabaseNotLoadedException {

    }

}

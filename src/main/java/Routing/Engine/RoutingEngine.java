package Routing.Engine;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import com.leastfixedpoint.json.JSONReader;
import com.leastfixedpoint.json.JSONSyntaxError;

import JSON.JSON;
import JSON.JSONCreator;
import JSON.JSONErrorWriter;
import JSON.JSONList;
import JSON.JSONResponseWriter;
import Routing.Graph.GraphFactory;
import Routing.Graph.StopGraph;
import Routing.Graph.TimeBoundedGraphFactory;
import Routing.Graph.TravelInformation;
import SQLDatabase.Config.SQLiteBaseConfigFactory;
import SQLDatabase.Database.Connections;
import SQLDatabase.Database.CurrentSQLiteDatabase;
import SQLDatabase.Database.GTFSDatabase;
import SQLDatabase.Database.GTFSSQLDatabase;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import Utils.Location.Global;
import Utils.Location.Location;
import Utils.Time.WalkTimeCalculator;

public class RoutingEngine {
    private JSONReader requestReader =
            new JSONReader(new InputStreamReader(System.in));
    private JSONResponseWriter responseWriter = new JSONResponseWriter();
    private JSONErrorWriter errorWriter = new JSONErrorWriter();
    private String ROUTING_ENGINE_STORAGE_DIRECTORY = "data/db/";

    public static final int FATAL_ERROR = -1;

    public static void main(String[] args) throws IOException {
        new RoutingEngine().run();
    }

    public void run() throws IOException {
        System.err.println("Starting");
        File dir = new File(ROUTING_ENGINE_STORAGE_DIRECTORY);
        if(!dir.exists())
            dir.mkdir();
        CurrentSQLiteDatabase.setCurrentConnection(Connections.JDBC_DATABASE_CONNECTION,new SQLiteBaseConfigFactory().getConfig());
        while (true) {
            Object json;
            try {
                json = requestReader.read();
            } catch (JSONSyntaxError e) {
                errorWriter.sendFatalError("Bad JSON input"); //Fatal error
                System.exit(0);
                break;
            } catch (EOFException e) {
                errorWriter.sendFatalError("End of input detected"); //Fatal error
                System.exit(0);
                break;
            }

            try {
                if (json instanceof Map<?, ?>) {
                    Map<?, ?> request = (Map<?, ?>) json;

                    if (request.containsKey("ping")) {
                        responseWriter.sendMessage(Map.of("pong", request.get("ping")));
                        continue;
                    } else if (request.containsKey("load")) {

                        if (sendZip((String) request.get("load")) == FATAL_ERROR)
                            System.exit(0);

                        continue;

                    } else if (request.containsKey("routeFrom") && request.containsKey("to") && request.containsKey("startingAt")) {

                        sendRoute(request, LocalDate.now().toString(), new Global());

                        continue;

                    } else if (request.containsKey("heatMapFrom") && request.containsKey("startingAt")) {

                        sendPaths(request, LocalDate.now().toString(), new Global(), new HashSet<>());

                        continue;
                    }
                    // ... process other requests here
                }
            }
            catch (Exception e) {

            }

            errorWriter.sendMessage("Bad request");
        }
    }

    /**
     *
     * @param path
     * @return -1 In case of a fatal error
     * @throws IOException
     */
    public int sendZip(String path) throws IOException {
        try {
            ZipFile zipFile = new ZipFile(path);
            GTFSDatabase database = new GTFSSQLDatabase();
            database.load(zipFile);
            responseWriter.sendMessage("loaded");
            return 1;
        }
        catch (IOException e){
            errorWriter.sendFatalError("File not found"); //Fatal error
            return FATAL_ERROR;
        } catch (SQLException e)
        {
            errorWriter.sendFatalError("ZIP file is missing one or more of the following files: {agency.txt, routes.txt, calendar.txt, calendar_dates.txt, trips.txt,stops.txt, stop_times.txt}"); //Fatal error
            return FATAL_ERROR;
        }
    }

    public JSONList sendRoute(Map<?,?> request, String date, Location city) throws IOException {
        try {
            if (!(request.get("routeFrom") instanceof Map<?, ?> || request.get("to") instanceof Map<?, ?>)) {
                errorWriter.sendMessage("Incorrect point format");
                return null;
            }
            Map<?, ?> routeFrom = (Map<?, ?>) request.get("routeFrom");
            Map<?, ?> to = (Map<?, ?>) request.get("to");
            if (!(routeFrom.containsKey("lat") || !routeFrom.containsKey("lon")
                    || !to.containsKey("lat") || !to.containsKey("lon"))) {
                errorWriter.sendMessage("Missing latitude or longitude at start or end");
                return null;
            }
            if (!((routeFrom.get("lat") instanceof Double) || !(routeFrom.get("lon") instanceof Double)
                    || !(to.get("lat") instanceof Double) || !(to.get("lat") instanceof Double))) {
                errorWriter.sendMessage("The coordinates are not correct");
                return null;
            }
            String startingAt = request.get("startingAt").toString();
            if (!WalkTimeCalculator.timeIsValid(startingAt)) {
                errorWriter.sendMessage("Invalid time format");
                return null;
            }
            DijkstraStarter starter = new DijkstraStarter();
            if (startingAt.length() == 5)
                startingAt += ":00";
            double startLat = Double.parseDouble(routeFrom.get("lat").toString());
            double startLon = Double.parseDouble(routeFrom.get("lon").toString());
            double endLat = Double.parseDouble(to.get("lat").toString());
            double endLon = Double.parseDouble(to.get("lon").toString());

            GraphFactory factory = new TimeBoundedGraphFactory();
            StopGraph graph = factory.getGraph(city,date,startingAt,startLat,startLon,endLat,endLon);
            List<TravelInformation> route = starter.getRouteFromTo(graph,startingAt);
            if (route.isEmpty()) {
                errorWriter.sendMessage("No existing path");
                return null;
            }
            JSONList jsonRoute = JSONCreator.formatRoute(route);

            responseWriter.sendMessage(jsonRoute);

            return jsonRoute;
        }
        catch (DatabaseNotLoadedException e)
        {
            errorWriter.sendMessage("Database is not properly loaded");
            return null;
        }
        catch (Exception e)
        {
            errorWriter.sendMessage("Unexpected error");
            return null;
        }
    }

    public JSON sendPaths(Map<?,?> request, String date, Location city, Set<String> closures) throws IOException {

        try {
            if (!(request.get("heatMapFrom") instanceof Map<?, ?> heatMapFrom)) {
                errorWriter.sendMessage("Incorrect point format");
                return null;
            }
            if (!(heatMapFrom.containsKey("lat")) || !(heatMapFrom.containsKey("lon"))) {
                errorWriter.sendMessage("Missing latitude or longitude at start or end");
                return null;
            }
            if (!((heatMapFrom.get("lat") instanceof Double) || !(heatMapFrom.get("lon") instanceof Double))) {
                errorWriter.sendMessage("The coordinates are not correct");
                return null;
            }
            String startingAt = request.get("startingAt").toString();
            if (!WalkTimeCalculator.timeIsValid(startingAt)) {
                errorWriter.sendMessage("Invalid time format");
                return null;
            }
            DijkstraStarter starter = new DijkstraStarter();
            if (startingAt.length() == 5)
                startingAt += ":00";
            double startLat = Double.parseDouble(heatMapFrom.get("lat").toString());
            double startLon = Double.parseDouble(heatMapFrom.get("lon").toString());

            GraphFactory factory = new TimeBoundedGraphFactory();
            StopGraph graph = factory.getGraphForHeat(city,date,startingAt,startLat,startLon);
            Map<String, TravelInformation> routes = starter.getForHeatMap(graph,startingAt, closures);
            if (routes.isEmpty()) {
                errorWriter.sendMessage("No existing paths");
                return null;
            }
            JSON jsonRoutes = JSONCreator.formatHeatMap(routes);

            return jsonRoutes;
        }
        catch (DatabaseNotLoadedException e)
        {
            errorWriter.sendMessage("Database is not properly loaded");
            return null;
        }
        catch (Exception e) {
            errorWriter.sendMessage("Unexpected error");
            return null;
        }
    }
}

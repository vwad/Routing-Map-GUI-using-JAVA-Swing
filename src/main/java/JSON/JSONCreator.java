package JSON;

import java.util.List;
import java.util.Map;

import Routing.Graph.TravelInformation;
import Utils.Time.DateCalculator;
import org.jxmapviewer.viewer.GeoPosition;

public class JSONCreator {

    public static JSON formatHeatMap(Map<String, TravelInformation> routes) {
        JSON jsonRoutes = new JSON();

        for(Map.Entry<String,TravelInformation> step : routes.entrySet())
        {

            jsonRoutes.put(step.getKey(),travelInformationToJson(step.getValue()));

        }

        return jsonRoutes;
    }

    public static JSON geoToJSON(GeoPosition geo)
    {
        return makePoint(geo.getLatitude(),geo.getLongitude());
    }

    private static JSON travelInformationToJson(TravelInformation step)
    {
        JSON stepJson = new JSON();

        stepJson.put("mode", step.isWalk() ? "walk" : "ride");
        stepJson.put("to", makePoint(step.getArrivalNode().getLat(), step.getArrivalNode().getLon()));
        stepJson.put("duration", step.getWeight() / 60);
        stepJson.put("startTime", DateCalculator.formatTimeSecToStringNoSeconds((int) step.getDepartureTime()));

        if (!step.isWalk()) {

            stepJson.put("stop", step.getArrivalPoint());
            stepJson.put("route", makeRouteInfo(step));

        }

        return stepJson;
    }

    public static JSONList formatRoute(List<TravelInformation> steps) {
        JSONList routeSteps = new JSONList();

        for (TravelInformation step : steps) {

            routeSteps.add(travelInformationToJson(step));

        }

        return routeSteps;
    }

    public static JSON getRouteInputFormat(double fromLat, double fromLon, double toLat, double toLon, String startTime)
    {
        JSON routeInput = new JSON();

        routeInput.put("routeFrom",makePoint(fromLat,fromLon));
        routeInput.put("to",makePoint(toLat,toLon));
        routeInput.put("startingAt",startTime);

        return routeInput;
    }

    public static JSON getHeatMapInputFormat(double fromLat, double fromLon, String startTime)
    {
        JSON routeInput = new JSON();

        routeInput.put("heatMapFrom",makePoint(fromLat,fromLon));
        routeInput.put("startingAt",startTime);

        return routeInput;
    }

    public static JSON makePoint(double lat, double lon)
    {
        JSON point = new JSON();

        point.put("lat", lat);
        point.put("lon", lon);

        return point;
    }

    private static JSON makeRouteInfo(TravelInformation step) {
        JSON route = new JSON();

        route.put("operator", step.getAgency_name());
        route.put("shortName", step.getRoute_short_name());
        route.put("longName", step.getRoute_long_name());
        route.put("headSign", step.getTrip_headsign());

        return route;
    }
}
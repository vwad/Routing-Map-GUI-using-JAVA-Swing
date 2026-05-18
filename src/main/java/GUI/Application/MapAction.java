package GUI.Application;

import GUI.Map.ControlPanel;
import GUI.Painter.HeatMapPainter;
import GUI.Painter.RoutePainter;
import GUI.Painter.StopsPainter;
import JSON.JSON;
import JSON.JSONCreator;
import JSON.JSONList;
import Routing.Engine.RoutingEngine;
import Routing.Graph.Node;
import SQLDatabase.Database.CurrentSQLiteDatabase;
import SQLDatabase.Query.QueryExecutor;
import Utils.Distance.CoordinatesCalculator;
import Utils.Location.Location;
import Utils.Time.DateCalculator;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class MapAction {
    private RoutingEngine engine;
    private StopsPainter waypointPainter;
    private Set<StopPoint> stopList;
    private RoutePainter routePainter;
    private HeatMapPainter heatMapPainter;
    private Set<String> closureStop;
    private Location city;
    private List<GeoPosition> storedPoints;
    private CompoundPainter<JXMapViewer> painterOverlay;
    private ControlPanel controlPanel;
    private JXMapViewer mapViewer;
    private LocalDateTime currDate;

    public MapAction(RoutingEngine engine,
                     Location city,
                     CompoundPainter<JXMapViewer> painterOverlay,
                     ControlPanel controlPanel,
                     JXMapViewer mapViewer) {
        this.stopList = new HashSet<>();
        this.waypointPainter = new StopsPainter();
        this.routePainter = new RoutePainter();
        this.heatMapPainter = new HeatMapPainter(city);
        this.closureStop = new HashSet<>();
        this.city = city;
        this.storedPoints = new ArrayList<>();
        this.engine = engine;
        this.painterOverlay = painterOverlay;
        this.controlPanel = controlPanel;
        this.mapViewer = mapViewer;
        this.currDate = LocalDateTime.now();
    }

    public void drawPointMap() {
        try {
            QueryExecutor executor = new QueryExecutor();
            List<Node> list = executor.getNodes(CurrentSQLiteDatabase.getCurrentConnection(),city);
            for (Node stop : list) {
                GeoPosition stopPos = new GeoPosition(stop.getLat(),stop.getLon());
                drawPoint(stopPos, stop.getId());
            }
        } catch (Exception e)  {
            e.printStackTrace();
        }
    }

    public void addClosures(Set<String> closures) {
        controlPanel.removeAllStops();
        for (String id : closures) {
            JLabel closure = new JLabel(id);
            closure.setFont(new Font("Arial", Font.PLAIN, 13));
            closure.setForeground(Color.red);
            controlPanel.addClosure(closure);
        }
    }

    public void drawPoint(GeoPosition pos, String stopId) {
        StopPoint point = new StopPoint(pos, stopId);
        if (closureStop.contains(stopId)) {
            point.setSelected(true);
        }
        stopList.add(point);
        waypointPainter.setWaypoints(stopList);
    }

    public void drawHeatMap(GeoPosition pos,String timestamp) {
        System.out.println("Heatmap from: " + pos + ", Current time: " + timestamp + ", Current Date: " + controlPanel.getLocalDate());
        try {
            String dateString = controlPanel.getLocalDate().toString();
            JSON routes = engine.sendPaths(JSONCreator.getHeatMapInputFormat(pos.getLatitude(),pos.getLongitude(),timestamp),dateString,city, closureStop);
            heatMapPainter.update(pos, routes, DateCalculator.formatTimeStringToSec(timestamp));
            refreshOverlay();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void drawRoute(List<GeoPosition> positions) {
        if(positions.size()<2)
            return;

        GeoPosition start = positions.get(positions.size()-2);
        GeoPosition end = positions.getLast();
        LocalDateTime date = positions.size()==2 ? LocalDateTime.of(controlPanel.getLocalDate(), LocalTime.parse(controlPanel.getEndTime().getText())) : currDate;

        System.out.println("Route from: " + start +
                ", Route to: " + end +
                ", Current time: "  + date.toLocalTime() +
                ", Current date: " +date.toLocalDate());
        try {
            JSONList list = engine.sendRoute(JSONCreator.getRouteInputFormat(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(),
                    date.toLocalTime().toString()), date.toLocalDate().toString(), city);
            double totalDuration = 1;
            for(JSON json : list)
            {
                totalDuration+=(Double) json.get("duration");
            }
            currDate = date.plusMinutes((long) totalDuration);
            routePainter.updateRoute(list);
            addTransferList(list, start);
            refreshOverlay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void redrawRoute() {
        if(!controlPanel.isRouteVisible()||storedPoints.size()<2)
            return;

        controlPanel.resetTripsDetails();
        routePainter.deleteRoute();

        routePainter.setStart(storedPoints.getFirst());
        List<GeoPosition> positions = new ArrayList<>();
        positions.add(storedPoints.getFirst());

        for (int i=1; i<storedPoints.size(); i++) {
            GeoPosition position = storedPoints.get(i);
            positions.add(position);
            drawRoute(positions);
        }
    }

    public void redrawHeat(GeoPosition pos, String time)
    {
        if(!controlPanel.isHeatVisible())
            return;
        drawHeatMap(pos,time);
    }

    public void addTransferList(JSONList list, GeoPosition start) {
        if(list.isEmpty())
            return;

        JSON previous = JSONCreator.geoToJSON(start);
        Set<String> uniqueHeadSign = new HashSet<>();
        double totalDuration = 1;
        double totalDistance = 0;
        for (JSON travelInfo : list) {
            JSON point = travelInfo.getJSON("to");
            totalDuration+=(Double)travelInfo.get("duration");
            totalDistance+=CoordinatesCalculator.calculateDistanceKMForJSONS(previous,point);
            String info = "walk";
            if (!travelInfo.get("mode").equals("walk")){
                JSON route = (JSON) travelInfo.get("route");
                info = route.get("headSign").toString();
                if (uniqueHeadSign.contains(info)) continue;
                uniqueHeadSign.clear();
                uniqueHeadSign.add(info);
            }
            String startTime = travelInfo.get("startTime").toString();
            JLabel stop = new JLabel(DateCalculator.fixTimeFormat(startTime)+" + "+info);
            stop.setFont(new Font("Arial", Font.PLAIN, 12));
            stop.setForeground(new Color(0, 102, 255));
            controlPanel.addTransfer(stop);

            previous = point;
        }

        totalDuration = Math.round(totalDuration);
        totalDistance = Math.round(totalDistance);

        controlPanel.plusDuration(totalDuration);
        controlPanel.plusDistance(totalDistance);


        controlPanel.updateEndTime(DateCalculator.addMinuteToTime(list.getLast().get("startTime").toString(),
                (Double)list.getLast().get("duration")+1));
    }

    public Set<StopPoint> getStopList() {return stopList;}
    public List<GeoPosition> getStoredPoints() {return storedPoints;}
    public RoutePainter getRoutePainter() {return routePainter;}
    public Set<String> getClosureStop() {return closureStop;}

    public void routeReset() {
        storedPoints.clear();
        stopList.clear();
        controlPanel.reset();
        waypointPainter.setWaypoints(stopList);
        routePainter.deleteRoute();
    }

    public void refreshOverlay() {
        List<Painter<JXMapViewer>> layers = new ArrayList<>();
        if (controlPanel.isHeatVisible()) {
            layers.add(heatMapPainter);
        }
        else {
            controlPanel.removeAllStops();
            heatMapPainter.deleteHeatMap();
            closureStop.clear();
        }
        if (controlPanel.isRouteVisible()) {
            layers.add(routePainter);
            waypointPainter.setWaypoints(stopList);
            layers.add(waypointPainter);
        }
        if (controlPanel.isClosureVisible()) {
            layers.add(waypointPainter);
        }
        painterOverlay.setPainters(layers);
        mapViewer.repaint();
    }

    public void addDay()
    {
        this.currDate = currDate.plusDays(1);
    }
}

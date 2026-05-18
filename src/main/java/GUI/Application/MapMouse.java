package GUI.Application;

import GUI.Map.ControlPanel;
import GUI.Painter.RoutePainter;
import Utils.Location.Location;
import Utils.Time.DateCalculator;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MapMouse extends MouseAdapter {
    private JXMapViewer mapViewer;
    private ControlPanel controlPanel;
    private List<GeoPosition> storedPoints;
    private List<GeoPosition> redrawnPoints;
    private RoutePainter routePainter;
    private Set<StopPoint> waypoints;
    private Set<String> closureStop;
    private MapAction actionManager;
    private GeoPosition currentPoint;
    private TripsInputsPanel tripsInputsPanel;
    private int radius = 8;

    public MapMouse(JXMapViewer mapViewer,
                    ControlPanel controlPanel,
                    MapAction mapAction, Location city) {
        this.mapViewer = mapViewer;
        this.controlPanel = controlPanel;
        this.storedPoints = mapAction.getStoredPoints();
        this.routePainter = mapAction.getRoutePainter();
        this.closureStop = mapAction.getClosureStop();
        this.waypoints = mapAction.getStopList();
        this.actionManager = mapAction;
        this.redrawnPoints = new ArrayList<>();
        this.tripsInputsPanel = controlPanel.getTripsInput();
        this.currentPoint = new GeoPosition(city.getCenterCoordinates());
        observeUpdates();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GeoPosition pos = mapViewer.convertPointToGeoPosition(e.getPoint());

        if (controlPanel.isRouteVisible()) {
            handleRouteAddClick(pos);
        }

        if (controlPanel.isHeatVisible() && !controlPanel.isClosureVisible()) {
            currentPoint = pos;
            handleHeatClick();
        }

        if (controlPanel.isClosureVisible()) {
            handleClosureClick(e.getPoint());

        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point ep = e.getPoint();
        if (controlPanel.isClosureVisible()) {
            for (StopPoint point : waypoints) {
                Point2D p = mapViewer.convertGeoPositionToPoint(point.getPosition());
                if (p.distance(ep) <= radius) {
                    controlPanel.setSelectedStops(point.getStopId());
                    break;
                } else {
                    controlPanel.setSelectedStops("000000000000");
                }
            }
        }
    }

    public void handleRouteAddClick(GeoPosition pos) {
        if(pos == null)
            return;

        String coordinates = String.format("%.5f",pos.getLatitude()) + " ," + String.format("%.5f",pos.getLongitude());
        storedPoints.add(pos);
        actionManager.drawPoint(pos,"");
        if(storedPoints.size()>1) {
            actionManager.drawRoute(storedPoints);
            tripsInputsPanel.addBox(coordinates, "End ");
        }
        else {
            routePainter.setStart(pos);
            tripsInputsPanel.addBox(coordinates, "Start ");
            actionManager.refreshOverlay();
        }

        int size = storedPoints.size()-1;
        tripsInputsPanel.getButton().addActionListener(e -> {tripsInputsPanel.wipeElements();handleRouteRemoveClick(size);});
    }
    
    public void handleRouteRemoveClick(int index) {
        redrawnPoints.addAll(storedPoints);
        redrawnPoints.remove(index);
        actionManager.routeReset();
        actionManager.refreshOverlay();
        for(GeoPosition position : redrawnPoints){

                handleRouteAddClick(position);

        }

        redrawnPoints.clear();
    }

    public void handleHeatClick() {
        int timeSec = DateCalculator.formatTimeStringToSec(controlPanel.getTime());
        actionManager.drawHeatMap(currentPoint,DateCalculator.formatTimeSecToString(timeSec));
    }

    public void handleResetClosure() {
        closureStop.clear();
        controlPanel.removeAllStops();
        for (StopPoint point : waypoints) {
            point.setSelected(false);
        }
        handleHeatClick();
    }

    public void handleClosureClick(Point2D ep) {
        for (StopPoint point : waypoints) {
            Point2D p = mapViewer.convertGeoPositionToPoint(point.getPosition());
            if (p.distance(ep) <= radius) {
                point.setSelected(!point.isSelected());
                if (point.isSelected()) {
                    closureStop.add(point.getStopId());
                } else {
                    closureStop.remove(point.getStopId());
                }
                actionManager.addClosures(closureStop);
                break;
            }
        }

        handleHeatClick();
    }

    public void observeUpdates()
    {
        TimePanel startTime = controlPanel.getTimeField();
        startTime.addActionListener(e -> actionManager.redrawRoute());
        startTime.addActionListener(e -> controlPanel.getEndTime().setText(startTime.getText()));
        startTime.addActionListener(e -> actionManager.redrawHeat(currentPoint,controlPanel.getTime()));

        CalendarPanel calendarPanel = controlPanel.getCalendar();
        calendarPanel.setRouteAction(e -> actionManager.redrawRoute());
        calendarPanel.setHeatAction(e -> actionManager.redrawHeat(currentPoint,controlPanel.getTime()));
    }

}

package GUI.Map;

import java.awt.*;

import javax.swing.*;

import GUI.Application.*;
import Routing.Engine.RoutingEngine;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;

import Utils.Location.Location;

public class MapCreator {
    private JXMapViewer mapViewer;
    private CompoundPainter<JXMapViewer> painterOverlay;
    private ControlPanel controlPanel;
    private MapAction mapAction;
    private MapMouse mouseAdapter;
    private HeatMapLegend heatMapLegend;

    public MapCreator(JPanel host, RoutingEngine engine, Location city) {
        mapViewer = new JXMapViewer();
        painterOverlay = new CompoundPainter<>();
        mapViewer.setOverlayPainter(painterOverlay);

        createSideControls(engine);

        TileFactoryInfo info = new OSMTileFactoryInfo(
                "osm", "jar:file:data/tiles/luxembourg_map_tiles.zip!"
        );
        mapViewer.setTileFactory(new DefaultTileFactory(info));

        double[] center = city.getCenterCoordinates();
        mapViewer.setAddressLocation(new GeoPosition(center[0], center[1]));
        mapViewer.setZoom(6);

        PanMouseInputListener pan = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(pan);
        mapViewer.addMouseMotionListener(pan);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.setLayout(new FlowLayout(FlowLayout.RIGHT));

        mapAction = new MapAction(engine,city,painterOverlay,controlPanel,mapViewer);
        mouseAdapter = new MapMouse(mapViewer,controlPanel,mapAction,city);
        mapViewer.addMouseListener(mouseAdapter);
        mapViewer.addMouseMotionListener(mouseAdapter);
        host.add(mapViewer, BorderLayout.CENTER);

        heatMapLegend = new HeatMapLegend();
        mapViewer.add(heatMapLegend);
        heatMapLegend.setLocation(mapViewer.getWidth()-heatMapLegend.getWidth(),0);

    }

    public void createSideControls(RoutingEngine engine) {
        controlPanel = new ControlPanel(engine);
        controlPanel.getHeatButton().addActionListener(e -> changeHeatMapMode());
        controlPanel.getRouteButton().addActionListener(e -> changeRouteMode());
        controlPanel.getClosureButton().addActionListener(e -> changeClosureMode());
        controlPanel.getResetButton().addActionListener(e -> {mouseAdapter.handleResetClosure();});
        controlPanel.getAddStopsButton().addActionListener(e ->{mouseAdapter.handleRouteAddClick(controlPanel.getAddStopCoordinates());controlPanel.setAddTexTFieldNull();});
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    private void changeHeatMapMode() {
        controlPanel.setHeatVisible(!controlPanel.isHeatVisible());
        if(controlPanel.isHeatVisible()) {
            controlPanel.getClosureButton().setVisible(true);
            controlPanel.setRouteVisible(true);
            changeRouteMode();
            controlPanel.getHeatButton().setBackground(new Color(100,255,100));
            visibleHeatmapLegend(true);
        } else {
            controlPanel.setClosureVisible(true);
            changeClosureMode();
            controlPanel.getClosureButton().setVisible(false);
            controlPanel.getHeatButton().setBackground(new Color(255, 255, 255));
            visibleHeatmapLegend(false);
        }
        refresh();
    }

    private void changeRouteMode() {
        controlPanel.setRouteVisible(!controlPanel.isRouteVisible());
        if(controlPanel.isRouteVisible()){
            controlPanel.visibleTripDetails(true);
            controlPanel.setHeatVisible(true);
            changeHeatMapMode();
            mapAction.getClosureStop().clear();
            controlPanel.getRouteButton().setBackground(new Color(100, 255, 100));
            controlPanel.getTripsInput().wipeElements();
        } else {
            controlPanel.visibleTripDetails(false);
            controlPanel.getRouteButton().setBackground(new Color(255, 255, 255));
            controlPanel.reset();
            mapAction.routeReset();
        }
        refresh();
    }

    private void changeClosureMode() {
        controlPanel.setClosureVisible(!controlPanel.isClosureVisible());
        if (controlPanel.isClosureVisible()) {
            controlPanel.visibleSelectedStop(true);
            controlPanel.getClosureButton().setBackground(new Color(100,255,100));
            mapAction.drawPointMap();
        } else {
            controlPanel.visibleSelectedStop(false);
            controlPanel.getClosureButton().setBackground(new Color(255, 255, 255));
            mapAction.getStopList().clear();
            mapAction.routeReset();
        }
        refresh();
    }

    private void refresh() {mapAction.refreshOverlay();}
    public void visibleHeatmapLegend(boolean status){heatMapLegend.setVisible(status);}

}



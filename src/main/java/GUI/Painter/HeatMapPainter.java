package GUI.Painter;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Map;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import GUI.Application.HeatMapCells;
import JSON.JSON;
import Utils.Location.Location;
import Utils.Time.DateCalculator;

public class HeatMapPainter implements Painter<JXMapViewer>{
    private GeoPosition start;
    private JSON routes;
    private HeatMapCells cells;
    private double startTime;
    public HeatMapPainter(Location city)
    {
        this.routes = new JSON();
        this.cells = new HeatMapCells(city);
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int width, int height){
        if(routes==null)

            return;

        HeatMapCells currentCells = cells;

        g = (Graphics2D) g.create();
        GeoPosition currentStart = start;
        Rectangle viewport = map.getViewportBounds();

        double currentTime = (int) (startTime/60);

        for(Map.Entry<String,Object> entry : routes.entrySet()){

            JSON travel =  (JSON)entry.getValue();
            JSON arrival = (JSON)travel.get("to");

            double value = ((double)travel.get("duration")+ (double) DateCalculator.formatTimeStringToSec((String) travel.get("startTime"))/60)-currentTime;

            if(value<=0)

                continue;

            GeoPosition pos = new GeoPosition((double)arrival.get("lat"),(double)arrival.get("lon"));

            currentCells.addValue(pos.getLongitude(),pos.getLatitude(),value);

        }

        currentCells.paint(g,map,viewport,currentStart);

        g.dispose();
    }


    public void deleteHeatMap()
    {
        this.start = null;
        this.routes.clear();
        this.startTime = -1;
        this.cells.resetCells();
    }

    public void update(GeoPosition start , JSON routes, double startTime)
    {
        this.start = start;
        this.routes = routes;
        this.startTime = startTime;
        this.cells.resetCells();
    }
}

package GUI.Painter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

import JSON.JSON;
import JSON.JSONList;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class RoutePainter implements Painter<JXMapViewer>
{
    private JSONList route;
    private GeoPosition start;
    private List<Color> colors;
    private Map<String, Color> colorMap;
    private static int count;

    public RoutePainter() {
        this.route = new JSONList();
        colorMap = new HashMap();
        colors = new ArrayList();
        count = -1;
        colors.add(Color.RED);
        colors.add(Color.MAGENTA);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
    }

    public void deleteRoute()
    {
        this.route.clear();
        start = null;
    }

    public void setStart(GeoPosition start)
    {
        this.start = start;
    }

    public void updateRoute(JSONList route)
    {
        if(route!=null) {
            this.route.addAll(route);
        }
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
        if(start == null|| route.isEmpty())
            return;
        g = (Graphics2D) g.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Rectangle viewport = map.getViewportBounds();

        drawLines(g, map, route, viewport);
        g.dispose();
    }

    private void drawLines(Graphics2D g, JXMapViewer map, JSONList points, Rectangle viewport) {
        JSON j1,j2;
        Point2D p1,p2;

        j2 = points.getFirst().getJSON("to");

        p1 = map.getTileFactory().geoToPixel(start,map.getZoom());
        p2 = map.getTileFactory().geoToPixel(new GeoPosition((double) j2.get("lat"),(double) j2.get("lon")),map.getZoom());

        drawLine(g,p1,p2,viewport,points.getFirst());

        for(int i = 0; i < points.size() - 1; i++)
        {
            j1 = points.get(i).getJSON("to");
            j2 = points.get(i+1).getJSON("to");
            p1 = map.getTileFactory().geoToPixel(new GeoPosition((double) j1.get("lat"),(double) j1.get("lon")),map.getZoom());
            p2 = map.getTileFactory().geoToPixel(new GeoPosition((double) j2.get("lat"),(double) j2.get("lon")),map.getZoom());
            drawLine(g,p1,p2,viewport,points.get(i+1));
        }
    }

    private void drawLine(Graphics2D g, Point2D p1, Point2D p2, Rectangle viewport, JSON transfer)
    {
        int x1 = (int) (p1.getX() - viewport.getX());
        int y1 = (int) (p1.getY() - viewport.getY());
        int x2 = (int) (p2.getX() - viewport.getX());
        int y2 = (int) (p2.getY() - viewport.getY());
        g = (Graphics2D) g.create();
        if (transfer.get("mode").equals("walk")) {
            float[] dashPattern = {3, 3};
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(
                    3,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10,
                    dashPattern,
                    0
            ));
        } else {
            JSON json = transfer.getJSON("route");
            String nameRoute = json.get("shortName").toString();
            if (!colorMap.containsKey(nameRoute)) {
                count++;
                if (count == colors.size()) count = 0;
                colorMap.put(nameRoute.toString(), colors.get(count));
            }
            Color c = colorMap.get(nameRoute);
            g.setColor(c);
            g.setStroke(new BasicStroke(4));
        }
        g.drawLine(x1, y1, x2, y2);
    }

}
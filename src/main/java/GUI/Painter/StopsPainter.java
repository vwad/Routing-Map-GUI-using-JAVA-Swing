package GUI.Painter;


import GUI.Application.StopPoint;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointPainter;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;


public class StopsPainter extends WaypointPainter<StopPoint> {

    public StopsPainter() {
        setRenderer(this::drawWaypoint);
    }

    private void drawWaypoint(Graphics2D g, JXMapViewer map, StopPoint sp) {
        Point2D pt = map.getTileFactory().geoToPixel(sp.getPosition(), map.getZoom());
        int x = (int) pt.getX();
        int y = (int) pt.getY();

        Color newColor = sp.getStopColor();
        drawPin(g, x, y, newColor);
    }

    private void drawPin(Graphics2D g, int x, int y, Color c) {
        // Parameters for the pin shape
        int pinRadius = 8;

        // Draw the circular head of the pin
        Ellipse2D.Double circle = new Ellipse2D.Double(x - pinRadius, y - pinRadius, pinRadius * 2, pinRadius * 2);

        // Draw the pointed tail (triangle)
        Path2D.Double tail = new Path2D.Double();
        tail.moveTo(x, y - pinRadius);           // bottom center of circle
        tail.lineTo(x - pinRadius, y - pinRadius * 3); // bottom left point
        tail.lineTo(x + pinRadius, y - pinRadius * 3); // bottom right point
        tail.closePath();

        g.setColor(c);
        g.fill(circle);
        g.fill(tail);

        // Draw the pin border
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        g.draw(tail);
        g.draw(circle);

        // Optionally, draw a smaller white circle inside to simulate the pinhole
        g.setColor(Color.WHITE);
        int innerRadius = pinRadius / 3;
        g.fillOval(x - innerRadius, y - innerRadius, innerRadius * 2, innerRadius * 2);
    }

}

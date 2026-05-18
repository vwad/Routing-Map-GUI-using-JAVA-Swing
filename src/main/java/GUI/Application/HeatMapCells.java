package GUI.Application;

import Utils.Location.Location;
import Utils.Distance.CoordinatesCalculator;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class HeatMapCells {
    private static List<HeatNode> mapCells;
    private static double MAX_VALUE = 30; //mins
    private static double CELL_SIZE; //km
    private static final double TRANSPARENCY = 40; //percentage
    private static final Color DEPARTURE_COLOR = Color.black;
    private static final int CELL_AMOUNT = 35000;
    private static final double START_POINT_RADIUS = 0.1;
    private static final int alphaValue = (int) (255 * (1-(TRANSPARENCY)/100));
    private static final double CELL_TO_VALUE_RATIO = 0.00318;
    private double dx;
    private double dy;
    private double startDx;
    private double startDy;
    private double minX;
    private double minY;
    private int rowSize;


    public HeatMapCells(Location city)
    {
        mapCells = new ArrayList<HeatNode>();
        initialize(city);
    }

    public static List<HeatNode> getMapCells()
    {
        return mapCells;
    }

    /**
     *
     * @return cell size on heatmap km
     */
    public static double getCellSize()
    {
        return CELL_SIZE;
    }

    /**
     *
     * @return max value seconds
     */
    public static double getMaxValueSeconds()
    {
        return MAX_VALUE*60;
    }

    public static double getMaxValue()
    {
        return MAX_VALUE;
    }


    private void initialize(Location city)
    {

        double[] leftUpper = city.getLeftUpperCornerCoordinates();
        double[] rightGround = city.getRightGroundCornerCoordinates();

        double area = CoordinatesCalculator.getArea(leftUpper,rightGround);
        CELL_SIZE = Math.sqrt(area/CELL_AMOUNT);
        MAX_VALUE = Math.round(CELL_SIZE/CELL_TO_VALUE_RATIO);

        this.minX = leftUpper[1];
        this.minY = rightGround[0];

        double[] dxAndDy = CoordinatesCalculator.getDistanceLatLon(CELL_SIZE,city.getCenterCoordinates());
        this.dx = dxAndDy[0];
        this.dy = dxAndDy[1];

        dxAndDy = CoordinatesCalculator.getDistanceLatLon(START_POINT_RADIUS,city.getCenterCoordinates());
        this.startDx = dxAndDy[0];
        this.startDy = dxAndDy[1];

        int counter = 0;

        for(double lat = rightGround[0];lat<leftUpper[0];lat+=dy) {

            for (double lon = leftUpper[1]; lon < rightGround[1]; lon += dx) {

                HeatNode cell = new HeatNode(lat,lon,counter++);
                mapCells.add(cell);

            }
            if(rowSize==0)

                rowSize = mapCells.size();

        }

    }

    /**
     * Paints the heatmap cell
     * @param g graphics
     * @param map map
     * @param viewport current viewport
     * @param start startPoint
     */
    public void paint(Graphics2D g, JXMapViewer map, Rectangle viewport, GeoPosition start)
    {
        for(HeatNode cell : mapCells)
        {

            GeoPosition pos = new GeoPosition(cell.getLat(),cell.getLon());

            Point2D rightDownCorner = map.getTileFactory().geoToPixel(new GeoPosition(pos.getLatitude()+dy,pos.getLongitude()+dx), map.getZoom());
            Point2D point = map.getTileFactory().geoToPixel(pos, map.getZoom());

            int Width = (int) (rightDownCorner.getX()-point.getX());
            int Height = (int) (point.getY()-rightDownCorner.getY());

            int X = (int) point.getX() - (int) (viewport.getX());
            int Y = (int) point.getY() - (int) (viewport.getY());

            double value = cell.getValue();

            Color color;

            if(value == 0 || value > MAX_VALUE)

                color = new Color(255,0,0,alphaValue);

            else

                color = (value <= MAX_VALUE/2)
                        ? new Color((int)(value * 255f/(MAX_VALUE/2)), 255, 0, alphaValue)
                        : new Color(255, 255-(int)((value-(MAX_VALUE/2)) *255f/(MAX_VALUE/2)), 0, alphaValue);


            g.setColor(color);
            g.fillRect(X, Y, Width, Height);

        }

        if(start!=null)
            drawStart(g,DEPARTURE_COLOR,start,map,viewport);

    }

    public void resetCells()
    {
        for(HeatNode cell : mapCells)
        {

            cell.resetValue();

        }
    }

    /**
     *
     * @param g graphics
     * @param color point color
     * @param pos
     * @param map
     * @param viewport
     */
    private void drawStart(Graphics2D g, Color color, GeoPosition pos, JXMapViewer map, Rectangle viewport)
    {
        Point2D rightDownCorner = map.getTileFactory().geoToPixel(new GeoPosition(pos.getLatitude()+startDy,pos.getLongitude()+startDx), map.getZoom());
        Point2D point = map.getTileFactory().geoToPixel(pos, map.getZoom());

        int Width = (int) (rightDownCorner.getX()-point.getX());
        int Height = (int) (point.getY()-rightDownCorner.getY());

        int X = (int) point.getX() - (int) (viewport.getX() + Width/2 );
        int Y = (int) point.getY() - (int) (viewport.getY() +  Height/2);


        g.setStroke(new BasicStroke(4));

        g.setColor(color);
        g.fillOval(X, Y, Width, Height);

        g.setColor(Color.BLACK);
        g.drawOval(X, Y, Width, Height);
    }

    private void addPointTo(int x, int y, double value)
    {

        if(y>=(mapCells.size()/rowSize))
            y=(mapCells.size()/rowSize)-1;
        if(y<0)
            y=0;
        if(x>=rowSize)
            x=rowSize-1;
        if(x<0)
            x=0;
        HeatNode cell = mapCells.get(x+y*rowSize);
        cell.setValue(value);
    }



    public void addValue(double x, double y, double value)
    {
        double diffX = x-minX;
        double diffY = y-minY;

        int xPointer = (int) (diffX/dx);
        int yPointer = (int) (diffY/dy);

        addPointTo(xPointer,yPointer,value);

    }

}

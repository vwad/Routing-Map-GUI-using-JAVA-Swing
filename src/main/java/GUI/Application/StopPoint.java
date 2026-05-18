package GUI.Application;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;

public class StopPoint extends DefaultWaypoint {
    private String stopId;
    private boolean isSelected;
    private Color stopColor;

    public StopPoint(GeoPosition pos, String id) {
        super(pos);
        this.stopId = id;
        this.isSelected = false;
        this.stopColor = new Color(82, 117, 255);
    }

    public String getStopId() {
        return stopId;
    }

    public Color getStopColor() {return stopColor;}

    public void setStopColor(Color stopColor) {this.stopColor = stopColor;}

    public void setSelected(boolean selected) {
        isSelected = selected;
        if(isSelected)
            this.stopColor = Color.red;
        else
            this.stopColor = new Color(82, 117, 255);;
    }

    public boolean isSelected() {
        return isSelected;
    }
}

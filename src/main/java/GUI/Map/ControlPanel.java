package GUI.Map;

import GUI.Application.*;

import Routing.Engine.RoutingEngine;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class ControlPanel extends JPanel {
    private JButton heatButton, routeButton, closureButton;
    private boolean heatVisible, routeVisible, closureVisible;
    private TimePanel time;
    private TripsDetailsPanel tripDetails;
    private SelectedStopPanel selectedStops;
    private CalendarPanel calendar;
    private TripsInputsPanel tripsInputsPanel;

    public ControlPanel(RoutingEngine engine) {
        setPreferredSize(new Dimension(250, 0));
        heatVisible = false;
        routeVisible = false;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createMatteBorder(0,0,0,1,Color.BLACK));

        setUpButtons(engine);
        setUpCalendar();
        setUpTime();
        setUpTripDetails();
        setUpClosureDetails();

        visibleTripDetails(false);
        this.tripsInputsPanel = tripDetails.getTripsInputPanel();
    }


    public void setUpClosureDetails() {
        selectedStops = new SelectedStopPanel();
        selectedStops.setVisible(false);
        add(selectedStops);
        add(Box.createVerticalStrut(10));
    }

    private void setUpTimeTextField()
    {
        time = new TimePanel(BoxLayout.X_AXIS);
    }
    private void setUpTime() {
        JLabel timeLabel = new JLabel("Start time: ");
        setUpTimeTextField();
        Box timeBox = new Box(BoxLayout.X_AXIS);
        timeBox.setMaximumSize(new Dimension(240, 25));
        timeBox.add(timeLabel);
        timeBox.add(Box.createHorizontalStrut(5));
        timeBox.add(time);
        add(timeBox);
        add(Box.createVerticalStrut(10));
    }

    private void setUpButtons(RoutingEngine engine) {

        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        buttonBox.add(Box.createVerticalStrut(10));
        JButton fileButton = new FileUploadButton(engine);
        fileButton.setMaximumSize(new Dimension(120, 25));
        fileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        fileButton.setFocusable(false);
        fileButton.setBackground(new Color(255, 255, 255));
        buttonBox.add(Box.createVerticalStrut(10));
        buttonBox.add(fileButton);

        heatButton = new JButton("HeatMap");
        heatButton.setMaximumSize(new Dimension(120, 25));
        heatButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        heatButton.setFocusable(false);
        heatButton.setBackground(new Color(255, 255, 255));
        buttonBox.add(Box.createVerticalStrut(10));
        buttonBox.add(heatButton);

        closureButton = new JButton("Closure");
        closureButton.setMaximumSize(new Dimension(100, 15));
        closureButton.setFocusable(false);
        closureButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closureButton.setBackground(new Color(255, 255, 255));
        closureButton.setVisible(false);
        buttonBox.add(Box.createVerticalStrut(5));
        buttonBox.add(closureButton);

        routeButton = new JButton("Plan Route");
        routeButton.setMaximumSize(new Dimension(120, 25));
        routeButton.setFocusable(false);
        routeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        routeButton.setBackground(new Color(255, 255, 255));
        buttonBox.add(Box.createVerticalStrut(5));
        buttonBox.add(routeButton);
        buttonBox.add(Box.createVerticalStrut(30));
        add(buttonBox);
        add(Box.createVerticalStrut(7));
    }
    public void setUpTripDetails() {
        tripDetails = new TripsDetailsPanel();
        add(tripDetails);
        add(Box.createVerticalStrut(10));
        tripDetails.setEndTime(time.getText());
    }
    public void setUpCalendar() {
        calendar = new CalendarPanel();
        calendar.setMaximumSize(new Dimension(180, 210));
        add(calendar);
        add(Box.createVerticalStrut(10));
    }

    public void addTransfer (JLabel transfer) {tripDetails.addTransfer(transfer);}
    public void plusDistance(double value) {tripDetails.plusDistance(value);}
    public void plusDuration(double value) {tripDetails.plusDuration(value);}
    public void addClosure(JLabel stopId) {selectedStops.addClosure(stopId);}

    public void visibleTripDetails(boolean status) {tripDetails.setVisible(status);}
    public void visibleSelectedStop(boolean status) {selectedStops.setVisible(status);}
    public void setHeatVisible(boolean heatVisible) {this.heatVisible = heatVisible;}
    public void setRouteVisible(boolean routeVisible) {this.routeVisible = routeVisible;}
    public void setClosureVisible(boolean closureVisible) {this.closureVisible = closureVisible;}
    public void setAddTexTFieldNull(){tripDetails.setAddText("");}
    public void setSelectedStops(String id) {selectedStops.setSelectedStop(id);}
    public boolean isHeatVisible() {return heatVisible;}
    public boolean isRouteVisible() {return routeVisible;}

    public boolean isClosureVisible() {return closureVisible;}
    public GeoPosition getAddStopCoordinates(){
        String[] coordinates = tripDetails.getAddText().split(",");

        if(coordinates.length!=2)
            return null;

        double xAxis = Double.parseDouble(coordinates[0]);
        double yAxis = Double.parseDouble(coordinates[1]);
        GeoPosition pos = new GeoPosition(xAxis,yAxis);
        return pos;
    }
    public TripsInputsPanel getTripsInput(){return tripsInputsPanel;}
    public JButton getHeatButton() {return heatButton;}
    public JButton getRouteButton() {return routeButton;}
    public JButton getAddStopsButton() {return tripDetails.getAddButton();}
    public JButton getClosureButton() {return closureButton;}
    public TimePanel getTimeField() {return time;}
    public JLabel getEndTime() {return tripDetails.getEndTime();}
    public JButton getResetButton() {return selectedStops.getResetBtn();}
    public LocalDate getLocalDate() {return calendar.getSelectedDate();}
    public String getTime() {return time.getText();}
    public void updateEndTime(String time) {tripDetails.updateEndTime(time);}
    public void removeAllStops() {selectedStops.removeAllClosure();}
    public void resetTripsDetails() {tripDetails.reset(time.getText());}
    public CalendarPanel getCalendar() {return calendar;}
    public void reset(){
        resetTripsDetails();
    }
}

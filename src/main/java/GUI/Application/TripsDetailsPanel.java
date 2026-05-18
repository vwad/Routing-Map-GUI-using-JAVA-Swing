package GUI.Application;

import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

public class TripsDetailsPanel extends JPanel {
    private JTextField add;
    private JButton addStopButton;
    private JLabel endTime;
    private JLabel totalTravelTime;
    private JLabel distance;
    private JPanel transferList;
    private double duration;
    private double distanceValue;
    private TripsInputsPanel tripsInputsPanel;
    

    public TripsDetailsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMaximumSize(new Dimension(250, 1000));
        duration = 0;
        distanceValue = 0;
        setUpTime();
        setUpDistance();
        setUpAddButton();
        setUpInputPoint();
        setUpTransferList();
    }

    public void setEndTime(String time) {
        this.endTime.setText(time);
    }

    public void setUpDistance() {
        Box distanceBox = new Box(BoxLayout.X_AXIS);
        distanceBox.setMaximumSize(new Dimension(190, 40));
        JLabel distanceLabel = new JLabel("Distance: ");
        distanceBox.add(distanceLabel);
        distanceBox.add(Box.createHorizontalStrut(10));
        distance = new JLabel("0km");
        distanceBox.add(distance);
        distanceBox.add(Box.createHorizontalGlue());
        add(distanceBox);
        add(Box.createVerticalStrut(10));
    }
    public void setUpTransferList() {
        Box transferBoxLabel = new Box(BoxLayout.X_AXIS);
        transferBoxLabel.setMaximumSize(new Dimension(190, 40));
        JLabel transferLabel = new JLabel("Transfer:");
        transferBoxLabel.add(transferLabel);
        transferBoxLabel.add(Box.createHorizontalStrut(130));
        add(transferBoxLabel);
        add(Box.createVerticalStrut(10));
        transferList = new JPanel();
        transferList.setLayout(new BoxLayout(transferList, BoxLayout.Y_AXIS));
        transferList.setAlignmentX(Component.CENTER_ALIGNMENT);
        JScrollPane transferScrollPane = new JScrollPane(transferList);
        transferScrollPane.setMaximumSize(new Dimension(230, 150));
        transferScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        transferScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(transferScrollPane);
        add(Box.createVerticalGlue());
    }
    public void setUpTime() {
        JLabel endTimeLabel = new JLabel("End time: ");
        endTime = new JLabel();
        Box endTimeBox = new Box(BoxLayout.X_AXIS);
        endTimeBox.setMaximumSize(new Dimension(190, 25));
        endTimeBox.add(endTimeLabel);
        endTimeBox.add(Box.createHorizontalStrut(10));
        endTimeBox.add(endTime);
        endTimeBox.add(Box.createHorizontalGlue());
        add(endTimeBox);
        add(Box.createVerticalStrut(10));

        Box totalTravelTimeBox = new Box(BoxLayout.X_AXIS);
        totalTravelTimeBox.setMaximumSize(new Dimension(190, 25));
        JLabel totalTravelTimeLabel = new JLabel("Total travel time: ");
        totalTravelTime = new JLabel("0 minutes");
        totalTravelTimeBox.add(totalTravelTimeLabel);
        totalTravelTimeBox.add(Box.createHorizontalStrut(10));
        totalTravelTimeBox.add(totalTravelTime);
        totalTravelTimeBox.add(Box.createHorizontalGlue());
        add(totalTravelTimeBox);
        add(Box.createVerticalStrut(10));
    }
    public void setUpInputPoint() {
        tripsInputsPanel = new TripsInputsPanel();
        JScrollPane tripsInputPanelSctoll = new JScrollPane(tripsInputsPanel);
        add(tripsInputPanelSctoll);

        add(Box.createVerticalStrut(10));
    }

    public void setUpAddButton()
    {
        JPanel manipulationButtonBox = new JPanel();
        manipulationButtonBox.setLayout(new BoxLayout(manipulationButtonBox, BoxLayout.Y_AXIS));
        manipulationButtonBox.add(Box.createVerticalStrut(10));

        Box addTextAndButton = new Box(BoxLayout.X_AXIS);
        add = new JTextField();
        add.setMaximumSize(new Dimension(100, 25));
        add.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add.setFocusable(true);
        addTextAndButton.add(add);

        addStopButton = new JButton("+");
        addStopButton.setMaximumSize(new Dimension(100, 25));
        addStopButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addStopButton.setFocusable(false);
        addStopButton.setBackground(new Color(255, 255, 255));
        addTextAndButton.add(addStopButton);
        manipulationButtonBox.add(addTextAndButton);

        add(manipulationButtonBox);
        add(Box.createVerticalStrut(10));
    }

    public void addTransfer(JLabel transfer) {
        transferList.add(Box.createVerticalStrut(5));
        transferList.add(transfer);
    }
    public void updateEndTime(String time) {
        endTime.setText(time);
    }
    public void plusDistance(double value) {
        distanceValue += value;
        distance.setText(String.format("%.2f",distanceValue)+"km");
    }
    public void plusDuration(double value) {
        duration += value;
        totalTravelTime.setText(duration+" minutes");
    }

    public JLabel getEndTime() {return endTime;}
    public TripsInputsPanel getTripsInputPanel(){return tripsInputsPanel;}

    public void reset(String time){
        totalTravelTime.setText("");
        distance.setText("");
        endTime.setText(time);
        transferList.removeAll();
        distanceValue = 0;
        duration = 0;
    }

    public JButton getAddButton()
    {
        return addStopButton;
    }

    public void setAddText(String text)
    {
        add.setText(text);
    }

    public String getAddText()
    {
        return add.getText();
    }
}

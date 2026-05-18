package GUI.Application;

import javax.swing.*;

import java.awt.*;

import static GUI.Application.HeatMapCells.getMaxValue;


public class HeatMapLegend extends JPanel{
    public HeatMapLegend(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setPreferredSize(new Dimension(70, 300));
        setVisible(false);
        upperPart();
        setGradient();
        downPart();
    }
    private void upperPart(){
        JLabel label = new JLabel("0 min");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);
    }
    private void setGradient(){
        Gradient gradient = new Gradient();
        gradient.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(gradient);
    }
    private void downPart(){
        JLabel label = new JLabel(String.valueOf((int)getMaxValue())+ " min");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);
    }

}

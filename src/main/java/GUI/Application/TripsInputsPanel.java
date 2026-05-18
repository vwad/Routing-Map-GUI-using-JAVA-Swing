package GUI.Application;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;


public class TripsInputsPanel extends JPanel{
    private List<Box> components;
    private List<JButton> buttons;
    private List<JLabel> labels;
    public TripsInputsPanel(){
        components = new ArrayList<>();
        buttons = new ArrayList<>();
        labels = new ArrayList<>();
        initialize();
    }
    private void initialize(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMaximumSize(new Dimension(250, 100));
    }
    public void addBox(String coordinates , String labelName){
        Box box = new Box(BoxLayout.X_AXIS);

        addNext(labelName);
        JLabel label = new JLabel(labelName + ":");
        label.setMaximumSize(new Dimension(50, 25));
        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
        labels.add(label);
        box.add(label);

        JTextField coordinatesField = new JTextField();
        coordinatesField.setMaximumSize(new Dimension(100, 25));
        coordinatesField.setAlignmentX(Component.CENTER_ALIGNMENT);
        coordinatesField.setFocusable(false);
        coordinatesField.setText(coordinates);
        box.add(coordinatesField);

        JButton removeButton = new JButton("-");
        removeButton.setMaximumSize(new Dimension(50, 25));
        removeButton.setFocusable(false);
        removeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        removeButton.setBackground(new Color(255, 255, 255));
        buttons.add(removeButton);
        box.add(removeButton);
        components.add(box);
        add(box);
        add(Box.createVerticalStrut(10));
        revalidate();
    }
  
    public JButton getButton(){
        return buttons.get(buttons.indexOf(buttons.getLast()));
    }
    public void addNext(String labelname){
        if(!(labelname.equals("Start "))){
            if(!(labels.getLast().getText().equals("Start "+":"))){
                labels.getLast().setText("Next :");
            }
        }
        repaint();
    }

    public void wipeElements(){
        buttons.clear();
        components.clear();
        labels.clear();
        removeAll();
        revalidate();
        repaint();
    }

}



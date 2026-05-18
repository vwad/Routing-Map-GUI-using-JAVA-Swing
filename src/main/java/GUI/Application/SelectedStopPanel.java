package GUI.Application;

import javax.swing.*;
import java.awt.*;

public class SelectedStopPanel extends JPanel {
    private JPanel stopsList;
    private JLabel selectedStop;
    private JButton resetBtn;

    public SelectedStopPanel() {
        setMaximumSize(new Dimension(230, 500));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        resetBtn = new JButton("Reset");
        resetBtn.setFocusable(false);
        resetBtn.setMaximumSize(new Dimension(120, 25));
        resetBtn.setBackground(new Color(255, 255, 255));
        add(Box.createVerticalStrut(10));
        add(resetBtn);
        add(Box.createVerticalStrut(10));
        selectedStop = new JLabel("Selected Stop: 000000000000");
        add(selectedStop);
        add(Box.createVerticalStrut(10));
        stopsList = new JPanel();
        stopsList.setLayout(new BoxLayout(stopsList, BoxLayout.Y_AXIS));
        JScrollPane stopsListScrollPane = new JScrollPane(stopsList);
        stopsListScrollPane.setMaximumSize(new Dimension(300, 500));
        stopsListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(stopsListScrollPane);
        add(Box.createVerticalGlue());
    }

    public void setSelectedStop(String id) {
        selectedStop.setText("Selected Stop: " + id);
    }

    public void addClosure(JLabel closure) {
        stopsList.add(Box.createVerticalStrut(5));
        stopsList.add(closure);
    }

    public JButton getResetBtn() {return resetBtn;}

    public void removeAllClosure() {stopsList.removeAll();}
}

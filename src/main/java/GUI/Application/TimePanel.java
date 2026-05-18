package GUI.Application;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;

public class TimePanel extends Box {
    private TimeComboBox hours;
    private TimeComboBox minutes;
    private TimeComboBox seconds;

    public TimePanel(int axis) {
        super(axis);
        init();
        reset();
    }

    private void init()
    {
        hours = new DefaultTimeComboBox(24);
        minutes = new DefaultTimeComboBox(60);
        seconds = new DefaultTimeComboBox(60);

        int strutSize = 5;
        add(hours);
        add(Box.createHorizontalStrut(strutSize));
        add(new JLabel(":"));
        add(Box.createHorizontalStrut(strutSize));
        add(minutes);
        add(Box.createHorizontalStrut(strutSize));
        add(new JLabel(":"));
        add(Box.createHorizontalStrut(strutSize));
        add(seconds);
    }

    public String getText()
    {
        return hours.getSelectedItem().toString()+":"+minutes.getSelectedItem().toString()+":"+seconds.getSelectedItem().toString();
    }

    private void reset()
    {
        LocalTime now = LocalTime.now();
        hours.setSelectedItem((now.getHour()<10?"0":"")+now.getHour());
        minutes.setSelectedItem((now.getMinute()<10?"0":"")+now.getMinute());
        seconds.setSelectedItem((now.getSecond()<10?"0":"")+now.getSecond());
        revalidate();
    }

    public void addActionListener(ActionListener l)
    {
        hours.addActionListener(l);
        minutes.addActionListener(l);
        seconds.addActionListener(l);
    }

}

package GUI.Application;

import javax.swing.*;

public abstract class TimeComboBox extends JComboBox<String> {

    protected void add(int n)
    {
        for(int i = 0; i < n; i++)
        {
            String time = (i < 10 ? "0" : "") + i;
            addItem(time);
        }
    }
}

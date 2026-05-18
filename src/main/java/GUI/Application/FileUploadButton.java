package GUI.Application;

import Routing.Engine.RoutingEngine;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FileUploadButton extends JButton {


    public FileUploadButton(RoutingEngine engine)
    {
        setText("Upload GTFS");
        addActionListener(e->uploadFIle(engine,this));
    }

    private void uploadFIle(RoutingEngine engine, JButton button)
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("ZIP file","zip"));
        int val = fileChooser.showOpenDialog(button);
        if(val == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            new NotificationWriter().sendNotification();
            try {
                engine.sendZip(file.getPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

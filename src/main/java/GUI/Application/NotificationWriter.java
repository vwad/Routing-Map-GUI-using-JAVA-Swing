package GUI.Application;

import Utils.Others.RoutingOutputStream;

import javax.swing.*;
import java.io.PrintStream;

public class NotificationWriter {

    /** Don't use with routing outputs, since it splits into pieces of multiples writes, preferably use for
     *  single message responses from engine. For example status and errors
     **/
    public void sendNotification()
    {
        RoutingOutputStream outputStream = new RoutingOutputStream();
        PrintStream outStream = RoutingOutputStream.getCurrentStream();
        outputStream.setCurrentStream(new PrintStream(outputStream)
        {

            @Override
            public void write(byte[] buf, int off, int len) {
                JOptionPane.showMessageDialog(null,new String(buf,off,len));
                outputStream.setCurrentStream(outStream);
            }

            @Override
            public void write(byte[] buf){
                JOptionPane.showMessageDialog(null,new String(buf));
                outputStream.setCurrentStream(outStream);
            }
        });
    }
}

package GUI.Application;

import javax.swing.*;
import java.awt.*;

public class Gradient extends JPanel{
    public Gradient(){
        initiliaze();
    }
    public void initiliaze(){
        setMaximumSize(new Dimension(250, 500));
    
    }
    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        Color[] colors = new Color[]{Color.green,Color.yellow,Color.red};
        float[] floats = new float[]{0,0.5F,1};
        LinearGradientPaint paint = new LinearGradientPaint(0,0,0,this.getHeight(),floats,colors);
        g2.setPaint(paint);
        g2.fillRect(0,0,this.getWidth(),this.getHeight());
    }

}

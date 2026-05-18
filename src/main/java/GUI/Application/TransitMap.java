package GUI.Application;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import GUI.Map.MapCreator;
import Routing.Engine.RoutingEngine;
import SQLDatabase.Config.SQLiteBaseConfigFactory;
import SQLDatabase.Database.Connections;
import SQLDatabase.Database.CurrentSQLiteDatabase;
import Utils.Location.CurrentLocation;
import Utils.Location.Location;

public class TransitMap implements Application {
    private JFrame mainWindow;
    private MapCreator mapCreator;
    private RoutingEngine engine;
    private Location city;

    public static void main(String[] args) {
        Location location = CurrentLocation.CURRENT_LOCATION;
        SwingUtilities.invokeLater(() -> new TransitMap().setup(location));
    }

    @Override
    public void setup(Location location) {
        CurrentSQLiteDatabase.setCurrentConnection(
                Connections.JDBC_DATABASE_CONNECTION,
                new SQLiteBaseConfigFactory().getConfig()
        );

        city = location;
        engine = new RoutingEngine();

        mainWindow = new JFrame("Transit Map");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setMinimumSize(Toolkit.getDefaultToolkit().getScreenSize());
        mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainWindow.setLayout(new BorderLayout());

        JPanel mapPanel = new JPanel(new BorderLayout());
        mapCreator = new MapCreator(mapPanel, engine, city);

        mainWindow.add(mapPanel, BorderLayout.CENTER);
        mainWindow.add(mapCreator.getControlPanel(), BorderLayout.WEST);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }
}
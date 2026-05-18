package SQLDatabase.Database;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CurrentSQLiteDatabase {
    private static String currentConnection;
    private static SQLiteConfig config;
    public static Connection getCurrentConnection() throws SQLException {
        if(currentConnection == null)
            return null;
        return DriverManager.getConnection(currentConnection, config.toProperties());
    }

    public static void setCurrentConnection(String currentConnection, SQLiteConfig config)
    {
        if(CurrentSQLiteDatabase.currentConnection != null)
            return;
        CurrentSQLiteDatabase.currentConnection = currentConnection;
        CurrentSQLiteDatabase.config = config;
    }
}

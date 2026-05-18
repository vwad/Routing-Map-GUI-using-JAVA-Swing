package SQLDatabase.Database;

import JSON.JSONErrorWriter;
import SQLDatabase.Config.SQLiteBaseConfigFactory;
import SQLDatabase.GTFSReaders.*;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GTFSSQLDatabase implements GTFSDatabase {
    @Override
    public void load(ZipFile zipFile) throws IOException,SQLException {
        Map<String, InputStream> streams = new HashMap<>();
        Map<String, GTFSReader> orderedOperationList = initializeGTFSOrder();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            InputStream in = zipFile.getInputStream(entry);
            streams.put(entry.getName(),in);
        }
        Set<String> keys = new HashSet<>(orderedOperationList.keySet());
        keys.removeAll(streams.keySet());
        if((keys.isEmpty()) || (keys.size() == 1 && (keys.contains("calendar.txt") || keys.contains("calendar_dates.txt")))) {
            orderedGTFSReading(streams, orderedOperationList);
        }
        else {
            throw new SQLException();
        }

    }

    private Map<String,GTFSReader> initializeGTFSOrder()
    {
        Map<String,GTFSReader> orderedOperationList = new LinkedHashMap<>();
        orderedOperationList.put("agency.txt",new AgencyReader());
        orderedOperationList.put("routes.txt",new RoutesReader());
        orderedOperationList.put("calendar.txt",new CalendarReader());
        orderedOperationList.put("calendar_dates.txt",new CalendarDatesReader());
        orderedOperationList.put("trips.txt",new TripsReader());
        orderedOperationList.put("stops.txt",new StopsReader());
        orderedOperationList.put("stop_times.txt",new StopTimesReader());
        return orderedOperationList;
    }

    private void orderedGTFSReading(Map<String, InputStream> streams,Map<String,GTFSReader> orderedOperationList)
    {
        try
        {
            Connection c = CurrentSQLiteDatabase.getCurrentConnection();
            c.setAutoCommit(false);
            dropTables(c);
            for(Map.Entry<String,GTFSReader> entry : orderedOperationList.entrySet())
            {
                String name = entry.getKey();
                GTFSReader reader = entry.getValue();
                InputStream stream = streams.get(name);

                reader.read(stream,c);
            }
            addIndexes(c);
            c.commit();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

    }

    private void dropTables(Connection c) throws SQLException
    {
        Statement statement = c.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS Stop_times");
        statement.executeUpdate("DROP TABLE IF EXISTS Calendar_Dates");
        statement.executeUpdate("DROP TABLE IF EXISTS Trips");
        statement.executeUpdate("DROP TABLE IF EXISTS Routes");
        statement.executeUpdate("DROP TABLE IF EXISTS Agency");
        statement.executeUpdate("DROP TABLE IF EXISTS Stops");
        statement.executeUpdate("DROP TABLE IF EXISTS Calendar");
        c.commit();
    }

    private void addIndexes(Connection c) throws SQLException
    {
        Statement statement = c.createStatement();
        statement.executeUpdate("CREATE INDEX lon_idx on Stops(stop_lon);");
        statement.executeUpdate("CREATE INDEX lat_idx on Stops(stop_lat);");
        statement.executeUpdate("CREATE INDEX stop_departure_seconds_idx on Stop_times(departure_time_seconds)");
        statement.executeUpdate("CREATE INDEX stop_trip_idx on Stop_times(trip_id)");
        statement.executeUpdate("CREATE INDEX stop_trip_sequence_idx on Stop_times(stop_sequence)");
        statement.executeUpdate("CREATE INDEX calendar_date_idx on Calendar_Dates(service_date)");
        statement.executeUpdate("CREATE INDEX calendar_date_code_idx on Calendar_Dates(exception_type)");
        statement.executeUpdate("CREATE INDEX agency_name_idx on Agency(agency_name);");
        statement.executeUpdate("CREATE INDEX route_long_idx on Routes(route_long_name);");
        statement.executeUpdate("CREATE INDEX route_short_idx on Routes(route_short_name);");
        statement.executeUpdate("CREATE INDEX trip_headsign_idx on Trips(trip_headsign)");
        statement.executeUpdate("CREATE INDEX trip_service_idx on Trips(service_id)");
        c.commit();
    }

    public static void main(String[] args) throws IOException, SQLException {
        CurrentSQLiteDatabase.setCurrentConnection(Connections.JDBC_DATABASE_CONNECTION,new SQLiteBaseConfigFactory().getConfig());
        ZipFile zipFile = new ZipFile("data/gtfs/gtfs-20250527-20250621.zip");
        GTFSDatabase database = new GTFSSQLDatabase();
        database.load(zipFile);
    }
}

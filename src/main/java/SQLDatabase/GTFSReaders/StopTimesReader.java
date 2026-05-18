package SQLDatabase.GTFSReaders;

import Utils.Time.DateCalculator;
import Utils.Others.FormatTool;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class StopTimesReader implements GTFSReader {

    @Override
    public void read(InputStream stream, Connection c) {
        try{
            int counter = 0;
            int batch = 5000;
            Statement stmt = c.createStatement();
            stmt.executeUpdate("""
                    CREATE table Stop_times (
                    trip_id TEXT,
                    arrival_time_seconds INTEGER,
                    departure_time_seconds INTEGER,
                    stop_id TEXT NOT NULL,
                    stop_sequence INTEGER,
                    PRIMARY KEY (trip_id, stop_sequence),
                    FOREIGN KEY (stop_id) REFERENCES Stops(stop_id),
                    FOREIGN KEY (trip_id) REFERENCES Trips(trip_id)
                    );
                    """);
            String query = "INSERT INTO Stop_times (trip_id, arrival_time_seconds,  departure_time_seconds, stop_id,stop_sequence) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = c.prepareStatement(query);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            Map<String, Integer> gtfsRow = FormatTool.GTFSRowIndex(reader.readLine());
            if (gtfsRow.get("trip_id") == null || gtfsRow.get("arrival_time") == null ||
                    gtfsRow.get("departure_time") == null || gtfsRow.get("stop_id") == null ||
                    gtfsRow.get("stop_sequence") == null) {
                System.out.println(gtfsRow);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> formattedString = FormatTool.formatGTFSRowData(line);

                if (formattedString.size() < 5)

                    continue;

                String trip_id = formattedString.get(gtfsRow.get("trip_id"));
                String arrival_time = formattedString.get(gtfsRow.get("arrival_time"));
                String departure_time = formattedString.get(gtfsRow.get("departure_time"));
                String stop_id = formattedString.get(gtfsRow.get("stop_id"));
                String stop_sequence = formattedString.get(gtfsRow.get("stop_sequence"));
                String[] departure_time_parts = departure_time.split(":");
                String[] arrival_time_parts = arrival_time.split(":");

                if (departure_time_parts[0].length() < 2)
                    departure_time = 0 + departure_time;
                if (arrival_time_parts[0].length() < 2)
                    arrival_time = 0 + arrival_time;

                preparedStatement.setString(1, trip_id);
                preparedStatement.setInt(2, DateCalculator.formatTimeStringToSec(arrival_time));
                preparedStatement.setInt(3, DateCalculator.formatTimeStringToSec(departure_time));
                preparedStatement.setString(4, stop_id);
                preparedStatement.setString(5, stop_sequence);

                preparedStatement.addBatch();
                counter++;

                if (counter % batch == 0)

                    preparedStatement.executeLargeBatch();


            }

            preparedStatement.executeLargeBatch();
            preparedStatement.close();
            stmt.close();
            c.commit();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Connection c = null;
        try {
            GTFSReader reader = new StopTimesReader();
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:data/db/test.db");
            c.setAutoCommit(false);
            InputStream stream = new FileInputStream("data/gtfs/stop_times.txt");
            reader.read(stream, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


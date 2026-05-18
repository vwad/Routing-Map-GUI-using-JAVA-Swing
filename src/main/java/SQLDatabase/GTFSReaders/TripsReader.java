package SQLDatabase.GTFSReaders;

import Utils.Others.FormatTool;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class TripsReader implements GTFSReader {

    @Override
    public void read(InputStream stream, Connection c) {
        try{
            int counter = 0;
            int batch = 500;

            Statement stmt = c.createStatement();
            stmt.executeUpdate("""
                   CREATE TABLE Trips (
                       trip_id TEXT NOT NULL PRIMARY KEY,
                       route_id TEXT NOT NULL,
                       service_id TEXT NOT NULL, 
                       trip_headsign TEXT NOT NULL, 
                       FOREIGN KEY (route_id) REFERENCES Routes(route_id)
                   );
                   """);

            String query = "INSERT INTO Trips (trip_id, route_id, service_id, trip_headsign) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = c.prepareStatement(query);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            Map<String, Integer> gtfsRow = FormatTool.GTFSRowIndex(reader.readLine());
            if (gtfsRow.get("trip_id") == null || gtfsRow.get("route_id") == null ||
                    gtfsRow.get("service_id") == null || gtfsRow.get("trip_headsign") == null) {
                System.out.println(gtfsRow);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> formattedString = FormatTool.formatGTFSRowData(line);

                if (formattedString.size() < 4)

                    continue;

                String trip_id = formattedString.get(gtfsRow.get("trip_id"));
                String route_id = formattedString.get(gtfsRow.get("route_id"));
                String service_id = formattedString.get(gtfsRow.get("service_id"));
                String trip_headsign = formattedString.get(gtfsRow.get("trip_headsign"));

                preparedStatement.setString(1, trip_id);
                preparedStatement.setString(2, route_id);
                preparedStatement.setString(3, service_id);
                preparedStatement.setString(4, trip_headsign);
                preparedStatement.addBatch();
                counter++;
                if (counter % batch == 0)

                    preparedStatement.executeLargeBatch();

            }

            preparedStatement.executeLargeBatch();
            preparedStatement.close();
            stmt.close();
            c.commit();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection c = null;
        try {
            GTFSReader rr = new TripsReader();
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:data/db/test.db");
            c.setAutoCommit(false);
            InputStream stream = new FileInputStream("data/gtfs/trips.txt");
            rr.read(stream, c);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

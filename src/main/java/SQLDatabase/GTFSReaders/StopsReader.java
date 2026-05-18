package SQLDatabase.GTFSReaders;

import Utils.Others.FormatTool;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class StopsReader implements GTFSReader {


    public static void main(String[] args) {
        Connection c = null;
        try {
            StopsReader sr = new StopsReader();
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:data/db/test.db");
            InputStream stream = new FileInputStream("data/gtfs/stops.txt");
            sr.read(stream, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void read(InputStream stream, Connection c) {
        try {

            int counter = 0;
            int batch = 500;

            Statement stmt = c.createStatement();
            stmt.executeUpdate("""
                   CREATE TABLE Stops (
                       stop_id TEXT PRIMARY KEY ,
                       stop_name TEXT,
                       stop_lat REAL,
                       stop_lon REAL
                   );
                   """);

            String query = "INSERT INTO Stops (stop_id, stop_name, stop_lat, stop_lon) VALUES ( ?, ?, ?, ?)";
            PreparedStatement preparedStatement = c.prepareStatement(query);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            Map<String, Integer> gtfsRow = FormatTool.GTFSRowIndex(reader.readLine());
            if (gtfsRow.get("stop_id") == null || gtfsRow.get("stop_name") == null ||
                    gtfsRow.get("stop_lat") == null || gtfsRow.get("stop_lon") == null) {
                System.out.println(gtfsRow);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> formattedString = FormatTool.formatGTFSRowData(line);
                if (formattedString.size() < 5)

                    continue;

                String stop_id = formattedString.get(gtfsRow.get("stop_id"));
                String stop_name = formattedString.get(gtfsRow.get("stop_name"));
                String stop_lat = formattedString.get(gtfsRow.get("stop_lat"));
                String stop_lon = formattedString.get(gtfsRow.get("stop_lon"));

                preparedStatement.setString(1, stop_id);
                preparedStatement.setString(2, stop_name);
                preparedStatement.setDouble(3, Double.parseDouble(stop_lat));
                preparedStatement.setDouble(4, Double.parseDouble(stop_lon));

                preparedStatement.addBatch();
                counter++;

                if (counter % batch == 0)

                    preparedStatement.executeLargeBatch();

            }

            preparedStatement.executeLargeBatch();
            preparedStatement.close();
            stmt.close();
            c.commit();

            c.commit();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

}


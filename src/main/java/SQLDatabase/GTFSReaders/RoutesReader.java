package SQLDatabase.GTFSReaders;

import Utils.Others.FormatTool;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Map;


public class RoutesReader implements GTFSReader {

    @Override
    public void read(InputStream stream, Connection c) {
        try {
            int count = 0;
            int batchSize = 500;

            Statement stmt = c.createStatement();
            stmt.executeUpdate("""
                    CREATE TABLE Routes (
                        route_id TEXT PRIMARY KEY,
                        agency_id TEXT,
                        route_short_name TEXT,
                        route_long_name TEXT,
                        route_type TEXT,
                        FOREIGN KEY (agency_id) REFERENCES Agency(agency_id)
                    );
                    """);

            String query = "INSERT INTO Routes (route_id, agency_id, route_short_name, route_long_name, route_type) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = c.prepareStatement(query);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            Map<String, Integer> gtfsRow = FormatTool.GTFSRowIndex(reader.readLine());

            if (gtfsRow.get("route_id") == null || gtfsRow.get("agency_id") == null ||
                    gtfsRow.get("route_short_name") == null || gtfsRow.get("route_long_name") == null ||
                    gtfsRow.get("route_type") == null) {
                System.out.println(gtfsRow);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> formattedString = FormatTool.formatGTFSRowData(line);

                if (formattedString.size() < 5)

                    continue;

                String route_id = formattedString.get(gtfsRow.get("route_id"));
                String agency_id = formattedString.get(gtfsRow.get("agency_id"));
                String route_short_name = formattedString.get(gtfsRow.get("route_short_name"));
                String route_long_name = formattedString.get(gtfsRow.get("route_long_name"));
                String route_type = formattedString.get(gtfsRow.get("route_type"));

                preparedStatement.setString(1, route_id);
                preparedStatement.setString(2, agency_id);
                preparedStatement.setString(3, route_short_name);
                preparedStatement.setString(4, route_long_name);
                preparedStatement.setString(5, route_type);

                preparedStatement.addBatch();
                count++;

                if (count % batchSize == 0)

                    preparedStatement.executeBatch();

            }
            stmt.executeUpdate("CREATE INDEX route_names_idx on Routes(route_long_name,route_short_name)");
            preparedStatement.executeBatch();
            c.commit();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection c = null;
        try {
            RoutesReader rr = new RoutesReader();
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:data/db/test.db");
            c.setAutoCommit(false);
            InputStream stream = new FileInputStream("data/gtfs/routes.txt");
            rr.read(stream, c);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

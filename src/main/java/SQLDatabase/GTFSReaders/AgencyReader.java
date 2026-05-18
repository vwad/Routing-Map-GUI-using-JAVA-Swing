package SQLDatabase.GTFSReaders;

import Utils.Others.FormatTool;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class AgencyReader implements GTFSReader {

    @Override
    public void read(InputStream stream, Connection c) {
        try {
            int count = 0;
            int batchSize = 500;

            Statement stmt = c.createStatement();
            stmt.executeUpdate("""
                    CREATE TABLE Agency (
                        agency_id TEXT PRIMARY KEY,
                        agency_name TEXT
                    );
                    """);
            String query = "INSERT INTO Agency (agency_id, agency_name) VALUES (?, ?)";

            PreparedStatement preparedStatement = c.prepareStatement(query);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            Map<String, Integer> gtfsRow = FormatTool.GTFSRowIndex(reader.readLine());
            if (gtfsRow.get("agency_id") == null || gtfsRow.get("agency_name") == null) {
                System.out.println(gtfsRow);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> formattedString = FormatTool.formatGTFSRowData(line);

                if (formattedString.size() < 2)

                    continue;

                String agency_id = formattedString.get(gtfsRow.get("agency_id"));
                String agency_name = formattedString.get(gtfsRow.get("agency_name"));
                preparedStatement.setString(1, agency_id);
                preparedStatement.setString(2, agency_name);

                preparedStatement.addBatch();
                count++;

                if (count % batchSize == 0)

                    preparedStatement.executeBatch();

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
            GTFSReader reader = new AgencyReader();
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:data/db/test.db");
            InputStream stream = new FileInputStream("data/gtfs/agency.txt");
            reader.read(stream, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
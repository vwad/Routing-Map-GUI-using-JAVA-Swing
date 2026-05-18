package SQLDatabase.GTFSReaders;

import Utils.Others.FormatTool;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class CalendarDatesReader implements GTFSReader {

    @Override
    public void read(InputStream stream, Connection c) {
        try {
            int counter = 0;
            int batch = 500;

            Statement stmt = c.createStatement();
            stmt.executeUpdate("""
                    CREATE TABLE Calendar_Dates (
                        service_id TEXT NOT NULL,
                        service_date TEXT NOT NULL,
                        exception_type INTEGER NOT NULL,
                        PRIMARY KEY (service_id, service_date)
                    );
                    """);

            if (stream == null)

                return;

            String query = "INSERT INTO Calendar_Dates (service_id, service_date, exception_type) VALUES (?, ?, ?)";

            PreparedStatement preparedStatement = c.prepareStatement(query);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            Map<String, Integer> gtfsRow = FormatTool.GTFSRowIndex(reader.readLine());
            if (gtfsRow.get("service_id") == null || gtfsRow.get("date") == null || gtfsRow.get("exception_type") == null) {
                System.out.println(gtfsRow);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> formattedString = FormatTool.formatGTFSRowData(line);

                if (formattedString.size() < 3)

                    continue;

                String service_id = formattedString.get(gtfsRow.get("service_id"));
                String service_date = formattedString.get(gtfsRow.get("date"));
                String exception_type = formattedString.get(gtfsRow.get("exception_type"));

                preparedStatement.setString(1, service_id);
                preparedStatement.setString(2, service_date);
                preparedStatement.setInt(3, Integer.parseInt(exception_type));

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
            GTFSReader reader = new CalendarDatesReader();
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:data/db/test.db");
            c.setAutoCommit(false);
            InputStream stream = new FileInputStream("data/gtfs/calendar_dates.txt");
            reader.read(stream, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

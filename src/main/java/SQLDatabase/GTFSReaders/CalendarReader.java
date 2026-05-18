package SQLDatabase.GTFSReaders;

import Utils.Others.FormatTool;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class CalendarReader implements GTFSReader {

    @Override
    public void read(InputStream stream, Connection c) {
        try {
            int counter = 0;
            int batch = 500;

            Statement stmt = c.createStatement();
            stmt.executeUpdate("""
                    CREATE TABLE Calendar (
                        service_id TEXT PRIMARY KEY,
                        monday INTEGER,
                        tuesday INTEGER,
                        wednesday INTEGER,
                        thursday INTEGER,
                        friday INTEGER,
                        saturday INTEGER,
                        sunday INTEGER,
                        start_date TEXT,
                        end_date TEXT
                    );
                    """);

            if (stream == null)

                return;

            String query = "INSERT INTO Calendar (service_id, monday, tuesday, wednesday, thursday," +
                    " friday, saturday, sunday,start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = c.prepareStatement(query);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            Map<String, Integer> gtfsRow = FormatTool.GTFSRowIndex(reader.readLine());

            if (gtfsRow.get("service_id") == null || gtfsRow.get("monday") == null || gtfsRow.get("tuesday") == null
                    || gtfsRow.get("wednesday") == null || gtfsRow.get("thursday") == null || gtfsRow.get("friday") == null || gtfsRow.get("saturday") == null
                    || gtfsRow.get("sunday") == null || gtfsRow.get("start_date") == null || gtfsRow.get("end_date") == null) {
                System.out.println(gtfsRow);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> formattedString = FormatTool.formatGTFSRowData(line);

                if (formattedString.size() < 10)

                    continue;

                String service_id = formattedString.get(gtfsRow.get("service_id"));
                String monday = formattedString.get(gtfsRow.get("monday"));
                String tuesday = formattedString.get(gtfsRow.get("tuesday"));
                String wednesday = formattedString.get(gtfsRow.get("wednesday"));
                String thursday = formattedString.get(gtfsRow.get("thursday"));
                String friday = formattedString.get(gtfsRow.get("friday"));
                String saturday = formattedString.get(gtfsRow.get("saturday"));
                String sunday = formattedString.get(gtfsRow.get("sunday"));
                String start_date = formattedString.get(gtfsRow.get("start_date"));
                String end_date = formattedString.get(gtfsRow.get("end_date"));

                preparedStatement.setString(1, service_id);
                preparedStatement.setInt(2, Integer.parseInt(monday));
                preparedStatement.setInt(3, Integer.parseInt(tuesday));
                preparedStatement.setInt(4, Integer.parseInt(wednesday));
                preparedStatement.setInt(5, Integer.parseInt(thursday));
                preparedStatement.setInt(6, Integer.parseInt(friday));
                preparedStatement.setInt(7, Integer.parseInt(saturday));
                preparedStatement.setInt(8, Integer.parseInt(sunday));
                preparedStatement.setString(9, start_date);
                preparedStatement.setString(10, end_date);

                preparedStatement.addBatch();
                counter++;

                if (counter % batch == 0)

                    preparedStatement.executeLargeBatch();

            }

            preparedStatement.executeLargeBatch();
            preparedStatement.close();
            stmt.close();
            c.commit();

        }
        catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Connection c = null;
        try {
            GTFSReader reader = new CalendarReader();
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:data/db/test.db");
            c.setAutoCommit(false);
            InputStream stream = new FileInputStream("data/gtfs/calendar.txt");
            reader.read(stream, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

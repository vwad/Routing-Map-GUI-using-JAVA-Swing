package Closure.Distributed;

import SQLDatabase.Config.SQLiteBaseConfigFactory;
import SQLDatabase.Database.Connections;
import SQLDatabase.Database.CurrentSQLiteDatabase;
import Utils.Location.City;
import Utils.Location.LuxembourgCity;
import Utils.Others.FormatTool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class PopulationDataReader {
    private static final String POPULATION_CSV_PATH = "data/ppp_LUX_2020_1km_Aggregated_UNadj.csv";

    public PopulationDataReader(Connection c, City city) throws IOException, SQLException
    {
        c.setAutoCommit(false);

        double[] leftGroundCorner = city.getLeftGroundCornerCoordinates();
        double[] rightUpperCorner = city.getRightUpperCornerCoordinates();

        Statement statement = c.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS POPULATION_PER_KM");
        statement.executeUpdate("CREATE TABLE POPULATION_PER_KM ( " +
                "POPULATION DOUBLE," +
                "lat DOUBLE, " +
                "lon DOUBLE)");

        PreparedStatement prepStatement = c.prepareStatement("INSERT INTO POPULATION_PER_KM VALUES (?,?,?)");

        BufferedReader reader = new BufferedReader(new FileReader(POPULATION_CSV_PATH));

        Map<String,Integer> columns = FormatTool.GTFSRowIndex(reader.readLine());

        String line;


        while ((line=reader.readLine())!=null)
        {
            List<String> row = FormatTool.formatGTFSRowData(line);
            double X = Double.parseDouble(row.get(columns.get("x")));
            double Y = Double.parseDouble(row.get(columns.get("y")));
            double Z = Double.parseDouble(row.get(columns.get("z")));

            if(X<leftGroundCorner[1]||X>rightUpperCorner[1])
                continue;

            if(Y<leftGroundCorner[0]||Y>rightUpperCorner[0])
                continue;


            System.out.println(X);
            System.out.println(Y);


            prepStatement.setDouble(1,Z);
            prepStatement.setDouble(2,Y);
            prepStatement.setDouble(3,X);

            prepStatement.addBatch();
        }

        prepStatement.executeLargeBatch();
        c.commit();
        c.setAutoCommit(true);

    }

    public static void main(String[] args) throws SQLException, IOException {
        CurrentSQLiteDatabase.setCurrentConnection(Connections.JDBC_DATABASE_CONNECTION,new SQLiteBaseConfigFactory().getConfig());
        Connection c = CurrentSQLiteDatabase.getCurrentConnection();

        new PopulationDataReader(c,new LuxembourgCity());
    }
}

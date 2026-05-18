package Closure.Basic;

import SQLDatabase.Config.SQLiteBaseConfigFactory;
import SQLDatabase.Database.Connections;
import SQLDatabase.Database.CurrentSQLiteDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class CreatePopulationTable {
    public CreatePopulationTable(Connection c) throws SQLException {
        c.setAutoCommit(false);
        Statement statement = c.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS POPULATION");
        statement.executeUpdate("CREATE TABLE POPULATION (location text primary key, " +
                "POPULATION DOUBLE," +
                "minLat DOUBLE, " +
                "minLon DOUBLE, " +
                "maxLat DOUBLE, " +
                "maxLon DOUBLE)");
        PreparedStatement prepStatement = c.prepareStatement("INSERT INTO POPULATION VALUES (?,?,?,?,?,?)");
        prepStatement.setString(1,"Bertrange");
        prepStatement.setInt(2,9097);
        prepStatement.setDouble(3,49.58272080743013);
        prepStatement.setDouble(4,6.001762041577446);
        prepStatement.setDouble(5,49.61721654958978);
        prepStatement.setDouble(6, 6.0691168831031925);
        prepStatement.addBatch();
        prepStatement.setString(1,"Contern");
        prepStatement.setInt(2,4716);
        prepStatement.setDouble(3,49.57453600973727);
        prepStatement.setDouble(4,6.201842376888037);
        prepStatement.setDouble(5,49.604526247793636);
        prepStatement.setDouble(6, 6.293252055016915);
        prepStatement.addBatch();
        prepStatement.setString(1,"Hesperange");
        prepStatement.setInt(2,17146);
        prepStatement.setDouble(3,49.558421043461635);
        prepStatement.setDouble(4, 6.143179025176369);
        prepStatement.setDouble(5,49.58986784968809);
        prepStatement.setDouble(6, 6.197595678635252);
        prepStatement.addBatch();
        prepStatement.setString(1,"Luxembourg-City");
        prepStatement.setInt(2,136208);
        prepStatement.setDouble(3,49.57878161667929);
        prepStatement.setDouble(4,6.080372346041426);
        prepStatement.setDouble(5,49.644481125714755);
        prepStatement.setDouble(6, 6.173632001765356);
        prepStatement.addBatch();
        prepStatement.setString(1,"Niederanven");
        prepStatement.setInt(2,6990);
        prepStatement.setDouble(3,49.62613039500594);
        prepStatement.setDouble(4,6.183220627142034);
        prepStatement.setDouble(5,49.66735479064206);
        prepStatement.setDouble(6, 6.260125536638009);
        prepStatement.addBatch();
        prepStatement.setString(1,"Sandweiler");
        prepStatement.setInt(2, 3874);
        prepStatement.setDouble(3,49.604099711116675);
        prepStatement.setDouble(4,6.194407336600775);
        prepStatement.setDouble(5,49.62790092778193);
        prepStatement.setDouble(6, 6.228567948709032);
        prepStatement.addBatch();
        prepStatement.setString(1,"Schuttrange");
        prepStatement.setInt(2,4466);
        prepStatement.setDouble(3,49.60719005202461);
        prepStatement.setDouble(4,6.239759760222286);
        prepStatement.setDouble(5,49.64077318286774);
        prepStatement.setDouble(6, 6.298553778549312);
        prepStatement.addBatch();
        prepStatement.setString(1,"Steinsel");
        prepStatement.setInt(2, 6018);
        prepStatement.setDouble(3,49.65796595012599);
        prepStatement.setDouble(4,6.103266863516797);
        prepStatement.setDouble(5,49.683629017955056);
        prepStatement.setDouble(6, 6.153048660056469);
        prepStatement.addBatch();
        prepStatement.setString(1,"Strassen");
        prepStatement.setInt(2,10637);
        prepStatement.setDouble(3,49.605717443146176);
        prepStatement.setDouble(4,6.061660013743528);
        prepStatement.setDouble(5,49.62572277820847);
        prepStatement.setDouble(6, 6.095916509187652);
        prepStatement.addBatch();
        prepStatement.setString(1,"Walferdange");
        prepStatement.setInt(2,	8937);
        prepStatement.setDouble(3,49.605717443146176);
        prepStatement.setDouble(4,6.061660013743528);
        prepStatement.setDouble(5,49.62572277820847);
        prepStatement.setDouble(6, 6.095916509187652);
        prepStatement.addBatch();
        prepStatement.setString(1,"Weiler-la-Tour");
        prepStatement.setInt(2,2519);
        prepStatement.setDouble(3,49.52546554438575);
        prepStatement.setDouble(4,6.176980578303634);
        prepStatement.setDouble(5,49.56932291767782);
        prepStatement.setDouble(6, 6.225012806756735);
        prepStatement.addBatch();
        prepStatement.executeBatch();
        c.commit();
        c.setAutoCommit(true);
    }

    public static void main(String[] args) throws SQLException {
        CurrentSQLiteDatabase.setCurrentConnection(Connections.JDBC_DATABASE_CONNECTION,new SQLiteBaseConfigFactory().getConfig());
        Connection c = CurrentSQLiteDatabase.getCurrentConnection();

        new CreatePopulationTable(c);
    }
}

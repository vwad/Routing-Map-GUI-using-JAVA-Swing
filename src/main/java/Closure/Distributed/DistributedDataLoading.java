package Closure.Distributed;

import SQLDatabase.Config.SQLiteBaseConfigFactory;
import SQLDatabase.Database.Connections;
import SQLDatabase.Database.CurrentSQLiteDatabase;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import Utils.Location.City;
import Utils.Location.LuxembourgCity;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DistributedDataLoading {
    public static void DataCalculation(City city,Connection c) throws DatabaseNotLoadedException, SQLException, IOException, InterruptedException {
        new DistributedCalculatingData(city,c);
        new DistributedRemovedStops(city,c,new DistributedSingleClosureThreadFactory());
        new DistributedRemovedStops(city,c,new DistributedTripleClosureThreadFactory());
    }

    public static void main(String[] args) throws SQLException, InterruptedException, DatabaseNotLoadedException, IOException {
        CurrentSQLiteDatabase.setCurrentConnection(Connections.JDBC_DATABASE_CONNECTION,new SQLiteBaseConfigFactory().getConfig());
        Connection c = CurrentSQLiteDatabase.getCurrentConnection();
        DistributedDataLoading.DataCalculation(new LuxembourgCity(),c);
    }


}

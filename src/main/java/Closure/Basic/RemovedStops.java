package Closure.Basic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Closure.ClosureInfo;
import Closure.ClosureThreadAbstractFactory;
import Closure.Trip;
import SQLDatabase.Config.SQLiteBaseConfigFactory;
import SQLDatabase.Database.Connections;
import SQLDatabase.Database.CurrentSQLiteDatabase;
import SQLDatabase.Exception.DatabaseNotLoadedException;
import Utils.Location.Location;
import Utils.Location.LuxembourgCity;

public class RemovedStops {
    
    public RemovedStops(Location city, Connection c, ClosureThreadAbstractFactory factory) throws SQLException, InterruptedException {
        List<ClosureInfo> info = new ArrayList<>();
        c.setAutoCommit(false);
        Statement statement = c.createStatement();
        ResultSet result = statement.executeQuery("Select * from Closure order by startDate");
        while(result.next())
        {
            double latA = result.getDouble(1);
            double lonA = result.getDouble(2);
            double latB = result.getDouble(3);
            double lonB = result.getDouble(4);
            Trip obj = new Trip(latA, lonA, latB, lonB);
            String startTime = result.getString(5);
            String date = result.getString(6);
            double travelTime = result.getDouble(7);
            int tripID = result.getInt(8);
            info.add(new ClosureInfo(obj, startTime, date, travelTime, tripID));
        }

        SingleClosureThread.startTime = System.currentTimeMillis();
        runThreads(c,city,info,factory);
        c.commit();
        c.setAutoCommit(true);
    }

    private void runThreads(Connection c, Location city, List<ClosureInfo> info, ClosureThreadAbstractFactory factory) throws InterruptedException, SQLException {
        final int threadCount = 6;
        List<Thread> threads = new ArrayList<>();
        int start = 0;
        int end = info.size()/threadCount;
        for(int i = 0; i<threadCount-1;i++)
        {
            Thread thread = new Thread(factory.getClosureRunnable(c,city,info,start,end),"Thread-"+i+1);
            thread.start();
            threads.add(thread);
            start = end;
            end += info.size()/threadCount;
        }
        end = info.size();
        Thread thread = new Thread(factory.getClosureRunnable(c,city,info,start,end),"Thread-"+threadCount);
        thread.start();
        threads.add(thread);

        for(Thread t: threads)
        {
            t.join();
        }
    }

    public static void main(String[] args) throws SQLException, DatabaseNotLoadedException, InterruptedException{
        CurrentSQLiteDatabase.setCurrentConnection(Connections.JDBC_DATABASE_CONNECTION,new SQLiteBaseConfigFactory().getConfig());
        Connection c = CurrentSQLiteDatabase.getCurrentConnection();
        new RemovedStops(new LuxembourgCity(), c, new TripleClosureThreadFactory());
    }

}

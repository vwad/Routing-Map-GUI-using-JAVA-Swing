package Closure.Basic;

import Closure.ClosureInfo;
import Closure.ClosureThreadAbstractFactory;
import Utils.Location.Location;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SingleClosureThreadFactory implements ClosureThreadAbstractFactory {

    @Override
    public Runnable getClosureRunnable(Connection c, Location city, List<ClosureInfo> info, int start, int end) throws SQLException {
        return new SingleClosureThread(c,city,info,start,end);
    }
}

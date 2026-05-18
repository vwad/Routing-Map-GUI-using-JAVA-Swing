package Closure.Distributed;

import Closure.ClosureInfo;
import Closure.ClosureThreadAbstractFactory;
import Utils.Location.Location;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DistributedTripleClosureThreadFactory  implements ClosureThreadAbstractFactory {
    @Override
    public Runnable getClosureRunnable(Connection c, Location city, List<ClosureInfo> info, int start, int end) throws SQLException {
        DistributedTripleClosureThread thread = new DistributedTripleClosureThread(c,city,info,start,end);
        if(DistributedTripleClosureThread.combinations == null)
            thread.getStopClosures();
        return thread;
    }
}

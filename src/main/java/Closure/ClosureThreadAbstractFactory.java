package Closure;

import Utils.Location.Location;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ClosureThreadAbstractFactory {
    Runnable getClosureRunnable(Connection c, Location city, List<ClosureInfo> info, int start, int end) throws SQLException;
}

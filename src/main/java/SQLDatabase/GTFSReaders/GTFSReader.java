package SQLDatabase.GTFSReaders;

import java.io.InputStream;
import java.sql.Connection;

public interface GTFSReader {
    void read(InputStream stream, Connection db);
}

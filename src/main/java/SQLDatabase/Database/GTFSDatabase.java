package SQLDatabase.Database;

import JSON.JSONBaseWriter;
import JSON.JSONErrorWriter;
import com.leastfixedpoint.json.JSONWriter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.zip.ZipFile;

public interface GTFSDatabase {
    void load(ZipFile zipFile) throws IOException, SQLException;
}

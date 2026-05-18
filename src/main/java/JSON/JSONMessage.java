package JSON;

import java.io.IOException;

public interface JSONMessage {
    void sendMessage(Object value) throws IOException;
}

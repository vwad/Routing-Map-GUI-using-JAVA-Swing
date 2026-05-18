package JSON;

import Utils.Others.RoutingOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class JSONErrorWriter extends JSONBaseWriter<OutputStreamWriter> {

    public JSONErrorWriter() {
        super(new OutputStreamWriter(new RoutingOutputStream()));
    }


    @Override
    public void sendMessage(Object value) throws IOException {     // Sends a non fatal error message
        write(Map.of("error", value.toString()));
        getWriter().write('\n');
        getWriter().flush();
    }


    public void sendFatalError(String message) throws IOException {   //sends a fatal error message
        sendMessage(message);
    }


}
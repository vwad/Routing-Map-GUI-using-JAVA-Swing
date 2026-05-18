package JSON;

import Utils.Others.RoutingOutputStream;
import com.leastfixedpoint.json.JSONNull;
import com.leastfixedpoint.json.JSONSerializable;
import com.leastfixedpoint.json.JSONSerializationError;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class JSONResponseWriter extends JSONBaseWriter{
    public JSONResponseWriter() {
        super(new OutputStreamWriter(new RoutingOutputStream()));
    }

    @Override
    public void sendMessage(Object value) throws IOException {
        write(Map.of("ok", value));
        getWriter().write('\n');
        getWriter().flush();
    }

    @Override
    public void write(Object object) throws IOException {
        if (object instanceof JSONNull) {
            this.emit("null");
        } else if (object instanceof JSONSerializable) {
            ((JSONSerializable) object).jsonSerialize(this);
        } else if (object instanceof Class) {
            this.string(object);
        } else if (object instanceof Boolean) {
            this.bool((Boolean) object);
        } else if (object instanceof Number) {
            this.number((Number) object);
        } else if (object instanceof String) {
            this.string(object);
        } else if (object instanceof Character) {
            this.string(object);
        } else if (object instanceof JSON) {
            this.json(object);
        } else if (object instanceof Map) {
            this.map((Map) object);
        } else if (object instanceof Iterable) {
            this.iterable((Iterable) object);
        } else if (object != null && object.getClass().isArray()) {
            this.array(object);
        } else {
            if (object != null || !this.allowJavaNull) {
                throw new JSONSerializationError("Cannot write object in JSON format: " + String.valueOf(object));
            }

            this.emit("null");
        }
    }

        public void json(Object object) throws IOException {
            JSON json = (JSON) object;
            emit(json);
        }

}

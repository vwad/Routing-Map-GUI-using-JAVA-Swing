package JSON;

import java.io.IOException;
import java.io.Writer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import com.leastfixedpoint.json.JSONWriter;

public abstract class JSONBaseWriter<W extends Writer> extends JSONWriter<W> implements JSONMessage{
    public JSONBaseWriter(W writer) {
        super(writer);
    }

    @Override
    protected void string(Object obj) throws IOException {
        emit('"');
        CharacterIterator it = new StringCharacterIterator(obj.toString());
        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            if (c == '"') emit("\"");
            else if (c == '\\') emit("\\\\");
            else if (c == '/') emit("\\/");
            else if (c == '\b') emit("\\b");
            else if (c == '\f') emit("\\f");
            else if (c == '\n') emit("\\n");
            else if (c == '\r') emit("\\r");
            else if (c == '\t') emit("\\t");
            else if (Character.isISOControl(c)) {
                emitUnicode(c);
            } else {
                emit(c);
            }
        }

        emit('"');
    }

}

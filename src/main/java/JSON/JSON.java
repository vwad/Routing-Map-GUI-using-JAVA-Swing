package JSON;

import java.util.LinkedHashMap;
import java.util.Map;

public class JSON extends LinkedHashMap<String, Object>{
    
    
    @Override
    public String toString()
    {
        StringBuilder bobTheBuilder = new StringBuilder();
        bobTheBuilder.append('{');
        boolean first = true;
        for(Map.Entry<String,Object> entry : entrySet())
        {
            if(first)

                first = false;

            else if(!first)

                bobTheBuilder.append(',');

            bobTheBuilder.append('"').append(entry.getKey()).append('"').append(':');

            if(entry.getValue() instanceof Double || entry.getValue() instanceof JSON ||
                    entry.getValue() instanceof JSONList || entry.getValue() instanceof Integer)

                bobTheBuilder.append(entry.getValue());

            else

                bobTheBuilder.append('"').append(entry.getValue()).append('"');


        }
        bobTheBuilder.append('}');

        return bobTheBuilder.toString();
    }

    public JSON getJSON(String key)
    {
        if(get(key) instanceof JSON json)

            return json;

        return null;
    }
}

package Utils.Others;

import java.util.*;

public class FormatTool {
    public static List<String> formatGTFSRowData(String rawLine)
    {
        List<String> segments = new ArrayList<>();
        StringBuilder currentSegment = new StringBuilder();
        boolean inQuotes = false;
        for(char c : rawLine.toCharArray())
        {

            if(c == ',' && !inQuotes)
            {
                segments.add(currentSegment.toString());
                currentSegment = new StringBuilder();
            }
            else if(c == '"')
                inQuotes = !inQuotes;
            else {
                currentSegment.append(c);
            }
        }
        segments.add(currentSegment.toString());
        return segments;
    }

    public static Map<String,Integer> GTFSRowIndex(String rawLine)
    {
        Map<String,Integer> gtfsRow = new HashMap<>();

        List<String> row = formatGTFSRowData(rawLine);
        for(int i = 0;i<row.size();i++)
        {
            gtfsRow.put(row.get(i).replace("\uFEFF", "").trim().toLowerCase(),i);
        }
        return gtfsRow;
    }
}

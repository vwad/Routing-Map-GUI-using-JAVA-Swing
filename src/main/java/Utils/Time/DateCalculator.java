package Utils.Time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateCalculator {


    public static int formatTimeStringToSec(String time) {
        String[] times = time.split(":");;
        int hour = Integer.parseInt(times[0]);
        int minutes = Integer.parseInt(times[1]);
        int seconds = 0;
        if(times.length>2)
            seconds = Integer.parseInt(times[2]);
        return hour*3600+minutes*60+seconds;
    }

    public static String addMinuteToTime(String time, double minutes)
    {
        time = fixTimeFormat(time);
        LocalTime localTime = LocalTime.parse(time,DateTimeFormatter.ofPattern("HH:mm"));
        return localTime.plusMinutes((long) minutes).toString()+":00";
    }

    public static String formatTimeSecToString(int time) {
        int hours = time/3600;
        int minutes = (time % 3600)/60;
        int seconds = time%60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String formatTimeSecToStringNoSeconds(int time)
    {
        int hours = time/3600;
        int minutes = (time % 3600)/60;
        return String.format("%02d:%02d", hours, minutes);
    }

    public static String fixTimeFormat(String time)
    {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        if(hour>=24)
            hour-=24;
        return (hour<10?"0"+hour:hour)+":"+parts[1];
    }


    public static String getDayFromDateString(String dateSting)
    {
        try {
            DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
            Date dt1 = format1.parse(dateSting);
            DateFormat format2 = new SimpleDateFormat("EEEE");
            return format2.format(dt1);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

package intech.juced.intechtest.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by juced on 14.01.2016.
 */
public class DateHelper {

    public static String getDateStringByFormat(Date date, String format){
        try {
            DateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            return sdf.format(date);
        }
        catch(Exception ex){
            return "";
        }
    }

    public static String getSongDurationString(int milliseconds) {
        try {
            Date date = new Date(milliseconds);
            DateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
            return sdf.format(date);
        }
        catch (Exception e) {
            return "00:00";
        }
    }

}

package pt.simov.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

    public static String getDateNow(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ");
        return mdformat.format(calendar.getTime());
    }

}

package com.yelo.com.mqttchat.Utilities;

/**
 * Created by moda on 29/06/17.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;


/**
 * To sort the message received after inserting new message in to the database locally,so messages are always stored in database in the sortedn order of time received
 */

public class MessageSorter implements Comparator {

    private Date date1, date2;


    @SuppressWarnings("unchecked")
    public int compare(Object firstObjToCompare, Object secondObjToCompare) {
        String firstDateString = (String) ((Map<String, Object>) firstObjToCompare).get("Ts");
        String secondDateString = (String) ((Map<String, Object>) secondObjToCompare).get("Ts");

        if (secondDateString == null || firstDateString == null) {
            return 0;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS z");


        try {

            date1 = sdf.parse(firstDateString);
            date2 = sdf.parse(secondDateString);


        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date1.after(date2)) return 1;
        else if (date1.before(date2)) return -1;
        else return 0;
    }

}

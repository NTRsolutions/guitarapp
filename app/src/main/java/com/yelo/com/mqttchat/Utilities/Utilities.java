package com.yelo.com.mqttchat.Utilities;

/**
 * Created by moda on 04/05/17.
 */


import com.yelo.com.mqttchat.AppController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by rahul on 25/3/16.
 */
public class Utilities {


    public static String tsInGmt() {


//        Log.d("log31",System.currentTimeMillis()+"");


        Date localTime = new Date(System.currentTimeMillis() - AppController.getInstance().getTimeDelta());
//        Log.d("log32",localTime.toString());

        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));


        String s = formater.format(localTime);

//        Log.d("log33",s);
        return s;
    }

    //converting time to localtime zone from gmt time

    public static String tsFromGmt(String tsingmt) {

        Date d = null;
        String s = null;

        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");

        try {
            d = formater.parse(tsingmt);
            TimeZone tz = TimeZone.getDefault();
            formater.setTimeZone(tz);
            s = formater.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return s;
    }


    public String gmtToEpoch(String tsingmt) {


        Date d = null;

        long epoch = 0;


        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");

        try {
            d = formater.parse(tsingmt);

            epoch = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return String.valueOf(epoch);
    }


    public static long Daybetween(String date1, String date2, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);

        sdf.setTimeZone(TimeZone.getDefault());


        Date startDate = null, endDate = null;
        try {
            startDate = sdf.parse(date1);
            endDate = sdf.parse(date2);
        } catch (Exception e) {
            e.printStackTrace();


            sdf = new SimpleDateFormat(pattern.substring(0, 19), Locale.ENGLISH);
            try {
                startDate = sdf.parse(date1);
                endDate = sdf.parse(date2);

            } catch (ParseException ef) {
                ef.printStackTrace();
            }
        }


        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        long daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;


    }

    public static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }


    public static String formatDate(String ts) {

        String s = null;
        Date d = null;


        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");

        try {
            d = formater.parse(ts);

            formater = new SimpleDateFormat("HH:mm:ss EEE dd/MMM/yyyy z");
            s = formater.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return s;

    }


    public static String epochtoGmt(String tsingmt) {


        Date d = null;
        String s = null;
        long epoch = 0;


        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        epoch = Long.parseLong(tsingmt);

        d = new Date(epoch);
        s = formater.format(d);


        return s;
    }

    public static String changeStatusDateFromGMTToLocal(String ts) {


        String s = null;
        Date d;


//        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");


        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");


        try {
            d = formater.parse(ts);

            TimeZone tz = TimeZone.getDefault();
            formater.setTimeZone(tz);

            s = formater.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return s;
    }


    /**
     * To get the epoch time of the current time in gmt
     */

    public static long getGmtEpoch() {


        Date localTime = new Date(System.currentTimeMillis() - AppController.getInstance().getTimeDelta());

        Date d;


        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));


        String tsingmt = formater.format(localTime);


        long epoch = 0;


        try {
            d = formater.parse(tsingmt);

            epoch = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return epoch;


    }


    /*
     * Get calls time based on the timezone
     */
    public static String tsFromGmtToLocalTimeZone(String tsingmt) {

//        Date d = null;
//        String s = null;
//
//        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");
//
//        try {
//            d = formater.parse(tsingmt);
//            TimeZone tz = TimeZone.getDefault();
//            formater.setTimeZone(tz);
//            s = formater.format(d);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return s;


        String s = null;
        Date d = null;


        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");

        try {
            d = formater.parse(tsingmt);

            formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            formater.setTimeZone(TimeZone.getDefault());

            s = formater.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return s;

    }

}
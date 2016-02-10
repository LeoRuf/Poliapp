package it.polito.mobilecourseproject.poliapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Enrico on 12/05/15.
 */
public class TimeManager {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getFormattedDate(Date date) {

        return getFormattedDate(date, "Published");
    }

    public static String getFormattedDate(Date date, String customString) {

        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        final long diff = now.getTime() - date.getTime();

        if (diff < MINUTE_MILLIS) {
            return customString+" just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return customString+" a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return customString+" " + diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return customString+" an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return customString+" " + diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return customString+" yesterday";
        } else if (diff < 24 * DAY_MILLIS ){
            return customString+" " + diff / DAY_MILLIS + " days ago";
        } else {
            return customString+" on "+df.format(date);

        }
    }


    public static String getFormattedTimestamp(Date date, String customString) {

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");


        final long diff = now.getTime() - date.getTime();

        if (diff < MINUTE_MILLIS) {
            return customString+" just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return customString+" a minute ago";
        } else if (diff < 59 * MINUTE_MILLIS) {
            return customString+" " + diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 61 * MINUTE_MILLIS) {
            return customString+" an hour ago";
        } else {
            return customString+" on "+dateFormat.format(date)+" at "+hourFormat.format(date);

        }
    }


    public static String getFormattedMessageDate(Date date) {

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");


        final long diff = now.getTime() - date.getTime();

        if (diff > 24 * HOUR_MILLIS) {
            return "on "+dateFormat.format(date)+" at "+hourFormat.format(date);
        } else {
            return hourFormat.format(date);
        }
    }







    public static int calculateAge(Date birthDate)
    {
        int years = 0;
        int months = 0;
        int days = 0;
        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());
        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        //Create new Age object
        return years;
    }
}

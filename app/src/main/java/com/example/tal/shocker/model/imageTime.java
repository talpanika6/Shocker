package com.example.tal.shocker.model;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Date;


public class imageTime {

    Date imageDate;

    public imageTime(Date date)
    {

        this.imageDate=date;


    }

    public void setImageTime(Date date)
    {
        this.imageDate=date;
    }
    public Date getImageDate()
    {
        return this.imageDate;
    }

    public String getCalcEstimateTimeDate()
    {
        DateTime myBirthDate = new DateTime(imageDate);
        DateTime now = new DateTime();
        Period period = new Period(myBirthDate, now);

        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendYears().appendSuffix("y")
                .appendMonths().appendSuffix("M")
                .appendWeeks().appendSuffix("w")
                .appendDays().appendSuffix("d")
                .appendHours().appendSuffix("h")
                .appendMinutes().appendSuffix("m")
                .printZeroNever()
                .toFormatter();

        String elapsed = formatter.print(period);

        return getByOneField(elapsed);
    }

    private String getByOneField(String time)
    {

        String field="";

        int end;

        if (time.contains("y"))
        {
            //year
            end=time.indexOf("y");
            field=time.substring(0,end+1);
        }
        else if (time.contains("M"))
        {
            //month
            end=time.indexOf("M");
            field=time.substring(0,end+1);
        }
        else if (time.contains("w"))
        {
            //week
            end=time.indexOf("w");
            field=time.substring(0,end+1);
        }
        else if (time.contains("d"))
        {
            //day
            end=time.indexOf("d");
            field=time.substring(0,end+1);
        }
        else if (time.contains("h"))
        {
            //hour
            end=time.indexOf("h");
            field=time.substring(0,end+1);
        }
        else if (time.contains("m"))
        {
            //min
            end=time.indexOf("m");
            field=time.substring(0,end+1);
        }


        return field;
    }
}

package com.poma.restaurant.utilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Calendar;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TimePicker;

public class CustomTimePickerMorning extends TimePickerDialog {

    private int minHour = -1, minMinute = -1, maxHour = 100, maxMinute = 100;

    private int currentHour, currentMinute;

    private Boolean min;

    public CustomTimePickerMorning(Context context, int theme_Holo_Light_Dialog_NoActionBar, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView, Boolean min) {
        //super(context, theme_Holo_Light_Dialog_NoActionBar, callBack, hourOfDay, minute, is24HourView);
        super(context, callBack, hourOfDay, minute, is24HourView);
        this.min=min;
    }

    public void setMin(int hour, int minute) {
        minHour = hour;
        minMinute = minute;
    }

    public void setMax(int hour, int minute) {
        maxHour = hour;
        maxMinute = minute;
    }

    //Min:6, Max:16
    //Min:16, Max:4
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        super.onTimeChanged(view, hourOfDay, minute);

        boolean validTime;
        if (maxHour>minHour)
            validTime=check_valid_time(hourOfDay, minute);
        else
            validTime=check_valid_time2(hourOfDay, minute);


        if(validTime) {
            currentHour = hourOfDay;
            currentMinute = minute;
        }
        else {
            if(min)
                updateTime(minHour, minMinute);
            else
                updateTime(maxHour, maxMinute);
        }
    }

    private boolean check_valid_time(int hourOfDay, int minute){
        boolean validTime;
        if(hourOfDay < minHour) {
            validTime = false;
        }
        else if(hourOfDay == minHour) {
            validTime = minute >= minMinute;
        }
        else if(hourOfDay == maxHour) {
            validTime = minute <= maxMinute;
        }
        else if(hourOfDay > maxHour) {
            validTime = false;
        }
        else {
            validTime = true;
        }
        return validTime;
    }

    //min:16, max:4
    private boolean check_valid_time2(int hourOfDay, int minute){
        boolean validTime;

        if (hourOfDay<maxHour || hourOfDay>minHour){
            validTime = true;
        }
        else {
            validTime = false;
        }

        if(hourOfDay == minHour) {
            validTime = minute >= minMinute;
        }
        else if(hourOfDay == maxHour) {
            validTime = minute <= maxMinute;
        }

        return validTime;
    }
}
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

    public CustomTimePickerMorning(Context context, int theme_Holo_Light_Dialog_NoActionBar, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        //super(context, theme_Holo_Light_Dialog_NoActionBar, callBack, hourOfDay, minute, is24HourView);
        super(context, callBack, hourOfDay, minute, is24HourView);
    }

    public void setMin(int hour, int minute) {
        minHour = hour;
        minMinute = minute;
    }

    public void setMax(int hour, int minute) {
        maxHour = hour;
        maxMinute = minute;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        super.onTimeChanged(view, hourOfDay, minute);

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

        if(validTime) {
            currentHour = hourOfDay;
            currentMinute = minute;
        }
        else {
            updateTime(currentHour, currentMinute);
        }
    }
}
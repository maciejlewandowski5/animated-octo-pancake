package com.example.mainactivity.helpers;


import android.content.Context;

import android.util.TypedValue;

import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Utils {

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static Date getFirstDayOfTheMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        return cal.getTime();
    }



    public static void toastError(Context context) {
        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
    }

    public static void toastMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public static String formatPriceLocale(float price) {
        return String.format(Locale.getDefault(), "%.2f", price);
    }

    public static float parsePriceLocale(String price, Context context) {
        DecimalFormat decimalFormat = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.getDefault()));
        try {
            return Objects.requireNonNull(decimalFormat.parse(price)).floatValue();
        } catch (ParseException e) {
            Utils.toastError(context);
            return 0;
        }
    }

    public static String trim(String string){
        if(string.length()>=25){
            string = string.substring(0,25) + "...";
        }
        return string.replaceAll("(\\r\\n|\\r|\\n)"," ");
    }



}

package com.maaps.expense.helpers;


import android.content.Context;

import android.graphics.Point;
import android.text.InputFilter;
import android.util.TypedValue;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maaps.expense.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import modelv2.UserSession;

public class Utils {

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int getScreenWidthPx(AppCompatActivity activity){
        return getScreenPointSize(activity).x;
    }
    private static Point getScreenPointSize(AppCompatActivity activity){
        Point screenSizes = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSizes);
        return  screenSizes;
    }
    public static int getScreenHeightPx(AppCompatActivity activity){
        return getScreenPointSize(activity).y;
    }

    public static Date getFirstDayOfTheMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        return cal.getTime();
    }

    public static String formatDateLocale(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "hh:mm dd-MM-yyyy",
                Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getLeaveGroupWarning(Context context){
       return context.getString(R.string.your_are_about_to_leave) +
                UserSession.getInstance().getCurrentShallowGroup().getGroupName() + "."
                + context.getString(R.string.not_see_content_not_going_back);
    }


    public static InputFilter[] priceFormatFilter(){
        return new InputFilter[]{new DecimalDigitsInputFilter(12, 2)};
    }


    public static void toastError(Context context) {
        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    public static void toastMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static void toastMessageLong(String message, Context context) {
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






}

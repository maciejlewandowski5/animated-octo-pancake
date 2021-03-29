package com.maaps.expense.helpers.expenseEditor;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.maaps.expense.DatePickerFragment;
import com.maaps.expense.TimePickerFragment;
import com.maaps.expense.helpers.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import modelv2.Expense;

public class DateTimePicker {
    private final TextView editDate;
    private int hour;
    private int minute;
    private int year;
    private int month;
    private int day;

    public DateTimePicker(TextView editDate) {
        this.editDate = editDate;


    }

    public void pick(AppCompatActivity activity) {
        DialogFragment newFragment = new TimePickerFragment(this);
        newFragment.show(activity.getSupportFragmentManager(), "timePicker");
        showDialog(activity);
    }

    public void showDialog(AppCompatActivity activity) {
        DialogFragment newFragment = new DatePickerFragment(this);
        newFragment.show(activity.getSupportFragmentManager(), "datePicker");
    }

    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        updateDateTime();
    }

    public Date getDateTime(){
        Date date = new GregorianCalendar(year,month,day,hour,minute).getTime();
        return date;
    }

    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        updateDateTime();
    }

    private void updateDateTime() {
        Date date = new GregorianCalendar(year,month,day,hour,minute).getTime();
        editDate.setText(Utils.formatDateLocale(date));
    }

    public void initialInputFromUserSession() {
        Calendar calendar = Calendar.getInstance();
        editDate.setText(Utils.formatDateLocale(calendar.getTime()));
        month=calendar.get(Calendar.MONTH);
        year=calendar.get(Calendar.YEAR);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        hour=calendar.get(Calendar.HOUR);
        minute=calendar.get(Calendar.MINUTE);
    }

    public void initialInputFromExpense(Expense expense) {


        Calendar calendar = Calendar.getInstance();

        editDate.setText(Utils.formatDateLocale(expense.getDateTime()));
        calendar.setTime(expense.getDateTime());
        month=calendar.get(Calendar.MONTH);
        year=calendar.get(Calendar.YEAR);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        hour=calendar.get(Calendar.HOUR);
        minute=calendar.get(Calendar.MINUTE);
    }
}

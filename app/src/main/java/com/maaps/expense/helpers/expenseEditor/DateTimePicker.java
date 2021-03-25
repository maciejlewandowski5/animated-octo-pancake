package com.maaps.expense.helpers.expenseEditor;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.maaps.expense.DatePickerFragment;
import com.maaps.expense.TimePickerFragment;
import com.maaps.expense.helpers.Utils;
import java.util.Calendar;
import java.util.Date;
import modelv2.Expense;

public class DateTimePicker {
    private final TextView editDate;
    private final Calendar calendar;

    public DateTimePicker(TextView editDate) {
        this.editDate = editDate;
        this.calendar = Calendar.getInstance();
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
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        updateDateTime();
    }

    public Date getDateTime(){
        return calendar.getTime();
    }

    public void setDate(int year, int month, int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONDAY, month);
        calendar.set(Calendar.DAY_OF_WEEK, day);
        updateDateTime();
    }

    private void updateDateTime() {
        editDate.setText(Utils.formatDateLocale(calendar.getTime()));
    }

    public void initialInputFromUserSession() {
        editDate.setText(Utils.formatDateLocale(new Date()));
    }

    public void initialInputFromExpense(Expense expense) {
        editDate.setText(Utils.formatDateLocale(expense.getDateTime()));
    }
}

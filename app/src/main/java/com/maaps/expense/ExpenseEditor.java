package com.maaps.expense;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.maaps.expense.helpers.DecimalDigitsInputFilter;
import com.maaps.expense.helpers.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import modelv2.UserSession;

public class ExpenseEditor extends AppCompatActivity {
    private static final String EXPENSE = "EXPENSE";

    private TextView editDate;
    private Calendar calendar;
    private LinearLayout container;
    private PopupMenu popupMenu;
    private TextView addBorrower;
    private TextView amount;
    private TextView payers;
    private SeekBar seekBar;


    ArrayList<modelv2.User> borrowers;
    modelv2.User payer;
    modelv2.Group group;
    modelv2.Expense expense;
    UserSession userSession;

    public void setTime(int hour, int minute) {
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        updateDateTime();
    }

    public void setDate(int year, int month, int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONDAY, month);
        calendar.set(Calendar.DAY_OF_WEEK, day);
        updateDateTime();
    }

    private void updateDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        editDate.setText(simpleDateFormat.format(calendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_expense_editor);
        Intent intent = getIntent();
        expense = (modelv2.Expense) intent.getSerializableExtra(EXPENSE);

        userSession = UserSession.getInstance();
        group = userSession.getCurrentGroup();
        borrowers = new ArrayList<>();
        calendar = Calendar.getInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment topBar = TopBar.newInstance(false);
        fragmentTransaction.replace(R.id.fragment, topBar);
        fragmentTransaction.commit();

        initializeViews();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd-MM-yyyy", Locale.getDefault());
        DecimalFormat format = new DecimalFormat("#.##");
        if (expense == null) {
            editDate.setText(simpleDateFormat.format(new Date()));
            payers.setText(userSession.getCurrentUser().getName());
            payer = userSession.getCurrentUser();
            seekBar.setMax(Float.valueOf((group.getMean() + group.getStandardDeviation()) * 100).intValue());
            seekBar.setProgress(Float.valueOf(group.getMean() * 100).intValue());
            amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(12, 2)});
            amount.setText(format.format(group.getMean()));

        } else {
            editDate.setText(simpleDateFormat.format(expense.getDateTime()));
            payers.setText(expense.getPayer().getName());
            ((TextView) findViewById(R.id.editTextTextPersonName)).setText(expense.getName());
            payer = expense.getPayer();
            borrowers.addAll(expense.getBorrowers());
            refreshDisplayedBorrowers(borrowers);
            seekBar.setMax(Float.valueOf((group.getMean() + group.getStandardDeviation()) * 100).intValue());
            seekBar.setProgress(Float.valueOf(group.getMean() * 100).intValue());
            amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(12, 2)});
            amount.setText(format.format(expense.getAmount()));
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DecimalFormat format = new DecimalFormat("#.##");
                amount.setText(format.format(progress / 100f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initializeViews() {
        addBorrower = findViewById(R.id.add_borrower);
        container = findViewById(R.id.container);
        editDate = findViewById(R.id.editTextDate);
        popupMenu = new PopupMenu(this, addBorrower);
        payers = findViewById(R.id.textView7);
        amount = findViewById(R.id.editTextNumber);
        seekBar = findViewById(R.id.seekBar);
    }

    private void populatePopupMenu(PopupMenu popupMenu) {
        for (modelv2.User user : group.getUsers()) {
            boolean userIsInBorrowers = false;
            for (modelv2.User borrower : borrowers) {
                if (user.getName().equals(borrower.getName())) {
                    userIsInBorrowers = true;
                    break;
                }
            }
            if (!userIsInBorrowers) {
                popupMenu.getMenu().add(user.getName());
            }
        }
        if (group.getUsers().size() != borrowers.size()) {
            popupMenu.getMenu().add(R.string.everyone);
        }
    }

    public void removeUser(View view) {
        FrameLayout frameLayout = (FrameLayout) view;
        TextView textView = (TextView) ((ConstraintLayout) frameLayout.getChildAt(0)).getChildAt(0);
        String userName = (String) textView.getText();
        ArrayList<modelv2.User> newBorrowers = new ArrayList<>();
        for (modelv2.User borrower : borrowers) {
            if (!userName.equals(borrower.getName())) {
                newBorrowers.add(borrower);
            }
        }
        borrowers = newBorrowers;
        refreshDisplayedBorrowers(newBorrowers);
    }

    private void displayBorrowers(ArrayList<modelv2.User> borrowers) {
        for (modelv2.User borrower : borrowers) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = BorrowerFragment.newInstance(borrower);
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out);
            fragmentTransaction.add(container.getId(), fragment, borrower.getName());
            fragmentTransaction.commit();
        }
    }

    private void refreshDisplayedBorrowers(ArrayList<modelv2.User> borrowers) {
        container.removeAllViews();
        displayBorrowers(borrowers);
    }

    public void addBorrower(View view) {
        if (group.getUsers().size() != borrowers.size()) {
            popupMenu = new PopupMenu(this, addBorrower);
            populatePopupMenu(popupMenu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    CharSequence itemTitle = item.getTitle();
                    for (modelv2.User user : group.getUsers()) {
                        if (itemTitle.equals(user.getName())) {
                            borrowers.add(user);
                        }

                    }
                    if (itemTitle.equals(getString(R.string.everyone))) {
                        borrowers.clear();
                        borrowers = (ArrayList<modelv2.User>) ((ArrayList<modelv2.User>) group.getUsers()).clone();
                    }
                    refreshDisplayedBorrowers(borrowers);
                    return false;
                }
            });

            popupMenu.show();
        } else {
            Utils.toastMessage("Everyone is already borrower.", this);
        }
    }

    public void addPayer(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        for (modelv2.User user : group.getUsers()) {
            popupMenu.getMenu().add(user.getName());
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CharSequence itemTitle = item.getTitle();
                for (modelv2.User user : group.getUsers()) {
                    if (itemTitle.equals(user.getName())) {
                        payer = user;
                    }
                }
                ((TextView) view).setText(payer.getName());
                return false;
            }
        });
        popupMenu.show();
    }

    public void pickDateTime(View view) {
        DialogFragment newFragment = new TimePickerFragment(this);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        showDatePickerDialog(view);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void sendExpense(View view) {
        String name = ((TextView) findViewById(R.id.editTextTextPersonName)).getText().toString();
        if (!amount.getText().toString().isEmpty() && !name.isEmpty() && !borrowers.isEmpty() && payer != null && calendar.getTime() != null) {
            float price = Float.parseFloat(amount.getText().toString().replace(",", "."));

            if (expense == null) {
                userSession.addExpense(name, ((Float) price).doubleValue(), calendar.getTime(), payer, borrowers);
            } else {
                modelv2.Expense expense1 = new modelv2.Expense(name, ((Float) price).doubleValue(), calendar.getTime(), payer, borrowers);
                expense1.setId(expense.getId());
                try {
                    userSession.editExpense(expense1);
                } catch (IllegalArgumentException e) {
                    Utils.toastMessageLong("This expense use, user who left group. You can not edit this expense. Please consider adding new expense.", this);
                }
            }
            onBackPressed();
        } else {
            Utils.toastMessage(getString(R.string.please_fill), this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userSession.setOnExpensePushed(new UserSession.OnExpensePushed() {
            @Override
            public void onExpensePushed() {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        userSession.removeOnExpensePushed();
    }
}
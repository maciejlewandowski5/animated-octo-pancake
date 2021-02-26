package com.example.mainactivity;

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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mainactivity.helpers.DecimalDigitsInputFilter;
import com.example.mainactivity.helpers.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import model.Group;
import model.GroupManager;
import model.User;

public class ExpenseEditor extends AppCompatActivity {

    static final int DATE_DIALOG_ID = 0;
    private static TextView editDate;
    private static Calendar calendar;
    LinearLayout container;
    PopupMenu popupMenu;
    TextView addBorrower;
    TextView amount;
    SeekBar seekBar;
    ArrayList<User> borrowers;
    User payer;
    Group group;

    public static void setTime(int hour, int minute) {
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        updateDateTime();
    }

    public static void setDate(int year, int month, int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONDAY, month);
        calendar.set(Calendar.DAY_OF_WEEK, day);
        updateDateTime();
    }

    private static void updateDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd-MM-yyyy", Locale.getDefault());
        editDate.setText(simpleDateFormat.format(calendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_editor);

        borrowers = new ArrayList<>();
        group = new Group("XXXX","Żagle",new User("Ala"));
        payer = new User("Payer");
        calendar = Calendar.getInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment topBar = TopBar.newInstance(GroupManager.getInstance(), false);
        fragmentTransaction.replace(R.id.fragment, topBar);
        fragmentTransaction.commit();


        addBorrower = findViewById(R.id.add_borrower);
        container = findViewById(R.id.container);
        editDate = findViewById(R.id.editTextDate);
        popupMenu = new PopupMenu(this, addBorrower);
        TextView payer = findViewById(R.id.textView7);
        amount = findViewById(R.id.editTextNumber);
        seekBar = findViewById(R.id.seekBar);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd-MM-yyyy", Locale.getDefault());
        DecimalFormat format = new DecimalFormat("#.##");
        editDate.setText(simpleDateFormat.format(new Date()));
        payer.setText(group.getCurrentUser().getName());
        seekBar.setMax(Float.valueOf(group.getMaxAmount() * 100).intValue());
        seekBar.setProgress(Float.valueOf(group.getMeanAmount() * 100).intValue());
        amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(12, 2)});
        amount.setText(format.format(group.getMeanAmount()));
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

    private void populatePopupMenu(PopupMenu popupMenu) {
        for (User user : group.getUsers()) {
            boolean userIsInBorrowers = false;
            for (User borrower : borrowers) {
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
        ArrayList<User> newBorrowers = new ArrayList<>();
        for (User borrower : borrowers) {
            if (!userName.equals(borrower.getName())) {
                newBorrowers.add(borrower);
            }
        }
        borrowers = newBorrowers;
        refreshDisplayedBorrowers(newBorrowers);
    }

    private void displayBorrowers(ArrayList<User> borrowers) {
        for (User borrower : borrowers) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = BorrowerFragment.newInstance(borrower);
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.fade_out);
            fragmentTransaction.add(container.getId(), fragment, borrower.getName());//TODO::change for ID
            fragmentTransaction.commit();
        }
    }

    private void refreshDisplayedBorrowers(ArrayList<User> borrowers) {
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
                    for (User user : group.getUsers()) {
                        if (itemTitle.equals(user.getName())) {
                            borrowers.add(user);
                        }

                    }
                    if (itemTitle.equals(getString(R.string.everyone))) {
                        borrowers.clear();
                        borrowers = (ArrayList<User>) ((ArrayList<User>) group.getUsers()).clone();
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
        for (User user : group.getUsers()) {
            popupMenu.getMenu().add(user.getName());
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CharSequence itemTitle = item.getTitle();
                for (User user : group.getUsers()) {
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
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
        showDatePickerDialog(view);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void addExpense(View view) {
        //TODO:: Implement it
    }
}
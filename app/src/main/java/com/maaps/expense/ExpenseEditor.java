package com.maaps.expense;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;
import com.maaps.expense.helpers.ExpenseEditor.BorrowerPayerPicker;
import com.maaps.expense.helpers.ExpenseEditor.DateTimePicker;
import com.maaps.expense.helpers.Utils;

import modelv2.Expense;

import modelv2.UserSession;

public class ExpenseEditor extends AppCompatActivity {
    private static final String EXPENSE = "EXPENSE";

    private TextView amount;
    private SeekBar seekBar;
    private BorrowerPayerPicker borrowerPayerPicker;
    private DateTimePicker dateTimePicker;
    private modelv2.Group group;
    private modelv2.Expense expense;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_expense_editor);
        retrieveExpense();
        initializeNotViewsParameters();
        initializeViews();
        initializeInputValues();
        initializeSeekBarListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userSession.setOnExpensePushed(this::onBackPressed);
    }

    @Override
    protected void onPause() {
        super.onPause();
        userSession.removeOnExpensePushed();
    }

    private void initializeSeekBarListeners() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amount.setText(Utils.formatPriceLocale(progress / 100f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initializeInputValues() {
        if (expense == null) {
            initializeInputValuesFromGeneraGroupInformation();
        } else {
            initializeInputValuesFromExpense();
        }
    }

    private void initializeInputValuesFromGeneraGroupInformation() {
        dateTimePicker.initialInputFromUserSession();
        borrowerPayerPicker.initialDataFromUserSession();
        seekBar.setMax(Float.valueOf((
                group.getMean() + group.getStandardDeviation()) * 100/*as percent*/).intValue());

        seekBar.setProgress(Float.valueOf(group.getMean() * 100/*as percent*/).intValue());

        amount.setText(Utils.formatPriceLocale(group.getMean()));
    }

    private void initializeInputValuesFromExpense() {
        dateTimePicker.initialInputFromExpense(expense);
        ((TextView) findViewById(R.id.editTextExpenseName)).setText(expense.getName());
        borrowerPayerPicker.initialInputFromExpense(expense);
        seekBar.setMax(Float.valueOf((
                group.getMean() + group.getStandardDeviation()) * 100/*as percent*/).intValue());

        seekBar.setProgress(Float.valueOf(group.getMean() * 100/*as percent*/).intValue());

        amount.setText(Utils.formatPriceLocale(expense.getAmount().floatValue()));
    }

    private void retrieveExpense() {
        Intent intent = getIntent();
        expense = (modelv2.Expense) intent.getSerializableExtra(EXPENSE);
    }

    private void initializeNotViewsParameters() {
        userSession = UserSession.getInstance();
        group = userSession.getCurrentGroup();
        borrowerPayerPicker = new BorrowerPayerPicker(R.id.container,R.id.add_borrower,R.id.textView7,this);
        dateTimePicker = new DateTimePicker(findViewById(R.id.editTextDate));
    }

    private void initializeViews() {
        amount = findViewById(R.id.editTextNumber);
        amount.setFilters(Utils.priceFormatFilter());
        seekBar = findViewById(R.id.seekBar);

        TopBar topBar = TopBar.newInstance(true);
        TopBar.refreshTopBar(R.id.fragment, this, topBar);
    }

    public void sendExpenseClick(View view) {
        if (isEachInputFilled()) {
            attemptSendExpense();
            onBackPressed();
        } else {
            Utils.toastMessage(getString(R.string.please_fill), this);
        }
    }

    private void attemptSendExpense() {
        String expenseName = ((TextView) findViewById(R.id.editTextExpenseName)).getText().toString();
        float price = parsePriceFromInput();
        if (expense == null) {
            userSession.addExpense(expenseName,
                    ((Float) price).doubleValue(),
                    dateTimePicker.getDateTime(),
                    borrowerPayerPicker.getPayer(),
                    borrowerPayerPicker.getBorrowers());
        } else {
            Expense expense1 = prepareExpense(expenseName, price);
            tryEditingExpense(expense1);
        }
    }

    private Expense prepareExpense(String expenseName, Float price) {
        Expense expense1 = new Expense(
                expenseName,
                price.doubleValue(),
                dateTimePicker.getDateTime(),
                borrowerPayerPicker.getPayer(), borrowerPayerPicker.getBorrowers());
        expense1.setId(expense.getId());
        return expense1;
    }

    private void tryEditingExpense(Expense expense1) {
        try {
            userSession.editExpense(expense1);
        } catch (IllegalArgumentException userNotInGroup) {
            Utils.toastMessageLong(getString(R.string.user_left_group_you_can_not_edit), this);
        }
    }

    private float parsePriceFromInput() {
        return Float.parseFloat(amount.getText().toString().replace(",", "."));
    }

    private boolean isEachInputFilled() {
        String expenseName = ((TextView) findViewById(R.id.editTextExpenseName)).getText().toString();
        return !amount.getText().toString().isEmpty() &&
                !expenseName.isEmpty() &&
                !borrowerPayerPicker.getBorrowers().isEmpty() &&
                borrowerPayerPicker.getPayer() != null &&
                dateTimePicker.getDateTime() != null;
    }

    public void addPayerButtonClick(View view) {
        borrowerPayerPicker.addPayerButtonClick(view);
    }

    public void addBorrowerButtonClick(View view) {
        borrowerPayerPicker.addBorrowerButtonClick();
    }
    public void removeUserFromBorrowerClick(View view){
        borrowerPayerPicker.removeUserFromBorrowerClick(view);
    }
    public void pickDateTime(View view) {
    dateTimePicker.pick(this);
    }
}
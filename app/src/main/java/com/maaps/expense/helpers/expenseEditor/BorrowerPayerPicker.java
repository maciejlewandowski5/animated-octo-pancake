package com.maaps.expense.helpers.expenseEditor;

import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.maaps.expense.BorrowerFragment;
import com.maaps.expense.R;
import com.maaps.expense.helpers.Utils;
import java.util.ArrayList;
import modelv2.Expense;
import modelv2.User;
import modelv2.UserSession;

public class BorrowerPayerPicker {

    ArrayList<User> borrowers;
    modelv2.User payer;
    modelv2.Group group;
    private PopupMenu popupMenu;
    UserSession userSession;
    private final LinearLayout currentBorrowerContainer;
    private final TextView addBorrowerButton;
    private final AppCompatActivity activity;
    TextView payerTextView;


    public BorrowerPayerPicker(int currentBorrowerContainer,
                               int addBorrowerTextViewId,
                               int addPayerTextViewId,
                               AppCompatActivity activity) {
        userSession = UserSession.getInstance();
        borrowers = new ArrayList<>();
        group = userSession.getCurrentGroup();
        this.currentBorrowerContainer = activity.findViewById(currentBorrowerContainer);
        this.addBorrowerButton = activity.findViewById(addBorrowerTextViewId);
        this.payerTextView = activity.findViewById(addPayerTextViewId);
        this.activity = activity;
        popupMenu = new PopupMenu(activity, this.addBorrowerButton);
    }

    public void initialInputFromExpense(Expense expense) {
        payerTextView.setText(expense.getPayer().getName());
        this.payer = expense.getPayer();
        this.borrowers.addAll(expense.getBorrowers());
        refreshDisplayedBorrowers(borrowers);
    }

    public void initialDataFromUserSession() {
        payerTextView.setText(userSession.getCurrentUser().getName());
        this.payer = userSession.getCurrentUser();
    }

    private void populateBorrowersPopupMenu(PopupMenu popupMenu) {
        for (modelv2.User user : group.getUsers()) {
            if (!isUserInBorrowers(user)) {
                popupMenu.getMenu().add(user.getName());
            }
        }
        if (!isEveryOneABorrower()) {
            popupMenu.getMenu().add(R.string.everyone);
        }
    }

    private boolean isEveryOneABorrower() {
        return group.getUsers().size() == borrowers.size();
    }

    private boolean isUserInBorrowers(User user) {
        boolean userIsInBorrowers = false;
        for (User borrower : borrowers) {
            if (user.getId().equals(borrower.getId())) {
                userIsInBorrowers = true;
                break;
            }
        }
        return userIsInBorrowers;
    }

    public void removeUserFromBorrowerClick(View view) {
        String clickedBorrowerName = pullOutUserNameFromBorrowerFragment((FrameLayout) view);
        borrowers = specifyWhichUserFragmentWasClicked(clickedBorrowerName);
        refreshDisplayedBorrowers(borrowers);
    }

    private String pullOutUserNameFromBorrowerFragment(FrameLayout view) {
        TextView textView = (TextView) ((ConstraintLayout)
                view.getChildAt(0)).getChildAt(0);
        return (String) textView.getText();
    }

    private ArrayList<User> specifyWhichUserFragmentWasClicked(String userName) {
        ArrayList<User> newBorrowers = new ArrayList<>();
        for (User borrower : borrowers) {
            if (!userName.equals(borrower.getName())) {
                newBorrowers.add(borrower);
            }
        }
        return newBorrowers;
    }

    private void refreshDisplayedBorrowers(ArrayList<modelv2.User> borrowers) {
        currentBorrowerContainer.removeAllViews();
        for (modelv2.User borrower : borrowers) {
            BorrowerFragment.displayBorrower(
                    borrower,
                    currentBorrowerContainer.getId(),
                    activity);
        }
    }

    public void addBorrowerButtonClick() {
        if (!isEveryOneABorrower()) {
            popupMenu = new PopupMenu(activity, addBorrowerButton);
            popupMenu.setOnMenuItemClickListener(
                    this::borrowersPopUpMenuOnClickListener);

            populateBorrowersPopupMenu(popupMenu);
            popupMenu.show();
        } else {
            Utils.toastMessage("Everyone is already borrower.", activity);
        }
    }

    private boolean borrowersPopUpMenuOnClickListener(MenuItem clickedItem) {
        String clickedName = clickedItem.getTitle().toString();
        userNameClick(clickedName);
        everyoneClick(clickedName);
        refreshDisplayedBorrowers(borrowers);
        return false;
    }

    private void everyoneClick(String itemTitle) {
        if (itemTitle.equals(activity.getString(R.string.everyone))) {
            borrowers.clear();
            borrowers = (ArrayList<User>) group.getUsers().clone();
        }
    }

    private void userNameClick(String itemTitle) {
        for (User user : group.getUsers()) {
            if (itemTitle.equals(user.getName())) {
                borrowers.add(user);
            }
        }
    }

    public void addPayerButtonClick(View view) {
        PopupMenu popupMenu = new PopupMenu(activity, view);
        populatePayerPopUpMenu(popupMenu);
        popupMenu.setOnMenuItemClickListener(
                clickedItem -> payersPopUpMenuOnClickListener(clickedItem, (TextView) view));
        popupMenu.show();
    }

    private boolean payersPopUpMenuOnClickListener(MenuItem clickedItem, TextView buttonName) {
        CharSequence itemTitle = clickedItem.getTitle();
        for (modelv2.User user : group.getUsers()) {
            if (itemTitle.equals(user.getName())) {
                payer = user;
                break;
            }
        }
        buttonName.setText(payer.getName());
        return false;
    }

    private void populatePayerPopUpMenu(PopupMenu popupMenu) {
        for (User user : group.getUsers()) {
            popupMenu.getMenu().add(user.getName());
        }
    }

    public User getPayer() {
    return payer;
    }

    public ArrayList<User> getBorrowers() {
    return  borrowers;
    }
}
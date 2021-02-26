package model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

public class ExpenseManager implements Serializable {
    private List<Expense> expenses;

    public ExpenseManager() {
        this.expenses = new ArrayList<>();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void addExpense(Float amount, String name, User payer, List<User> borrowers) {
        expenses.add(new Expense(amount, name, payer, borrowers));
    }

    public float getTotalBallance() {
        float result = 0;
        for (Expense expense : expenses) {
            result += expense.getAmount();
        }
        return result;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    @Exclude
    float getUserBalance(User user) {
        float balance = 0f;
        for (Expense expense : expenses) {
            if (isUserPayer(user, expense)) {
                balance += expense.getAmount();
            } else {
                balance -= (expense.getAmount() / (float) expense.getNumberOfBorrowers());
            }
        }
        return balance;
    }

    @Exclude
    public int getUserPercentBalanceInExpensesTotalBalance(User user) {
        float groupTotalBalance = 0;
        float userTotalBalance = 0;
        for (Expense expense : expenses) {
            groupTotalBalance += expense.getAmount();
        }
        for (Expense expense : expenses) {
            if (isUserPayer(user, expense)) {
                userTotalBalance += expense.getAmount();
            }
        }

        if (groupTotalBalance == 0 || userTotalBalance == 0) {
            return 0;
        }

        int semicolonSwitcher = 100;
        return Math.round((userTotalBalance / groupTotalBalance) * semicolonSwitcher);

    }

    @Exclude
    List<Expense> getUserPayDebtExpenses(User mainUser, List<User> users) {
        List<Expense> debtExpenses = new ArrayList<>();
        for (User user : users) {
            if (!user.equals(mainUser)) {
                float balance = getUserToUserBalance(mainUser, user);

                if (balance > 0) {
                    List<User> borrowers = new ArrayList<>();
                    borrowers.add(mainUser);
                    debtExpenses.add(new Expense(balance, "Suggested exspense", user, borrowers));
                } else if (balance < 0) {
                    List<User> borrowers = new ArrayList<>();
                    borrowers.add(user);
                    debtExpenses.add(new Expense(-balance, "Suggested exspense", mainUser, borrowers));
                }
            }
        }
        return debtExpenses;
    }

    @Override
    public String toString() {
        return "ExpenseManager{" + '\n' +
                "expenses=" + expenses + '\n' +
                '}';
    }

    private boolean isUserPayer(User user, Expense expense) {
        return expense.getPayer().equals(user);
    }

    private boolean isUserBorrower(User user, Expense expense) {
        List<User> borrowers = expense.getBorrowers();
        for (User borrower : borrowers) {
            if (borrower.equals(user)) {
                return true;
            }
        }
        return false;
    }

    @Exclude
    private float getUserToUserBalance(User user1, User user2) {
        float userToUserBalance = 0;
        for (Expense expense : expenses) {
            if (isUserPayer(user1, expense) && isUserBorrower(user2, expense)) {
                userToUserBalance += expense.getAmount();
            }

            if (isUserPayer(user2, expense) && isUserBorrower(user1, expense)) {
                userToUserBalance -= expense.getAmount();
            }
        }
        return userToUserBalance;
    }

}

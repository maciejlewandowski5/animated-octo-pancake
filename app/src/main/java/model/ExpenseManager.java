package model;

import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {
    private List<Expense> expenses;

    public ExpenseManager() {
        this.expenses = new ArrayList<>();
    }

    public void addExpense(Expense expense){
        expenses.add(expense);
    }

    public void addExpense(Float amount, String name, User payer, List<User> borrowers){
        expenses.add(new Expense(amount, name, payer, borrowers));
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    @Override
    public String toString() {
        return "ExpenseManager{" + '\n' +
                "expenses=" + expenses + '\n' +
                '}';
    }
}

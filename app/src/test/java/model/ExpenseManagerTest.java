package model;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExpenseManagerTest {

    private final User payer = new User("payer");
    private final User borrower1 = new User("borrower1");
    private final User borrower2 = new User("borrower2");
    private final List<User> borrowers = new ArrayList<>();

    @Test
    public void addExpense_isCorrect() {
        ExpenseManager expenseManager = new ExpenseManager();
        borrowers.add(borrower1);
        borrowers.add(borrower2);
        expenseManager.addExpense(new Expense(231.31f, "Żabka", payer, borrowers));
        expenseManager.addExpense(123.12f, "Biedronka", payer, borrowers);
        System.out.println("addExpense_isCorrect:");
        System.out.println(expenseManager.toString());
    }


    @Test
    public void getUserBalance_isCorrect() {
        ExpenseManager expenseManager = new ExpenseManager();
        borrowers.add(borrower1);
        borrowers.add(borrower2);
        expenseManager.addExpense(new Expense(231.31f, "Żabka", payer, borrowers));
        expenseManager.addExpense(123.12f, "Biedronka", payer, borrowers);
        System.out.println("getUserBalance_isCorrect:");
        System.out.println(expenseManager.getUserBalance(payer));
        System.out.println(expenseManager.getUserBalance(borrower1));
        System.out.println(expenseManager.getUserBalance(borrower2));
    }

    @Test
    public void getUserPercentBalanceInGroupTotalBalance_isCorrect() {
        ExpenseManager expenseManager = new ExpenseManager();
        borrowers.add(borrower1);
        borrowers.add(borrower2);
        expenseManager.addExpense(new Expense(231.31f, "Żabka", payer, borrowers));
        expenseManager.addExpense(123.12f, "Biedronka", payer, borrowers);
        expenseManager.addExpense(new Expense(100.31f, "Żabka", borrower1, borrowers));
        System.out.println("getUserPercentBalanceInGroupTotalBalance_isCorrect:");
        System.out.println(expenseManager.getUserPercentBalanceInExpensesTotalBalance(payer));
        System.out.println(expenseManager.getUserPercentBalanceInExpensesTotalBalance(borrower1));
        System.out.println(expenseManager.getUserPercentBalanceInExpensesTotalBalance(borrower2));
        System.out.println(expenseManager.toString());
    }

    @Test
    public void getUserPayDebtExpenses_isCorrect() {
        ExpenseManager expenseManager = new ExpenseManager();
        borrowers.add(borrower1);
        borrowers.add(borrower2);
        expenseManager.addExpense(new Expense(100f, "Żabka", payer, borrowers));
        expenseManager.addExpense(50f, "Biedronka", payer, borrowers);
        expenseManager.addExpense(new Expense(100f, "Żabka", borrower1, borrowers));
        System.out.println("getUserPayDebtExpenses_isCorrect:");
        List<Expense> debtExpensesPayer = expenseManager.getUserPayDebtExpenses(payer, borrowers);
        for (Expense expense: debtExpensesPayer){
            System.out.println(expense.toString());
        }
        List<Expense> debtExpensesBorrower1 = expenseManager.getUserPayDebtExpenses(borrower1, borrowers);
        for (Expense expense: debtExpensesBorrower1){
            System.out.println(expense.toString());
        }
        List<Expense> debtExpensesBorrower2 = expenseManager.getUserPayDebtExpenses(borrower2, borrowers);
        for (Expense expense: debtExpensesBorrower2){
            System.out.println(expense.toString());
        }
    }
}
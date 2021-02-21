package model;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExpenseManagerTest {

    private final User payer = new User("1", "Bohdan");
    private final User borrower1 = new User("2", "Maciek");
    private final User borrower2 = new User("3", "Marcin");
    private final List<User> borrowers = new ArrayList<>();

    @Test
    public void addExpense_isCorrect() {
        ExpenseManager expenseManager = new ExpenseManager();
        borrowers.add(borrower1);
        borrowers.add(borrower2);
        expenseManager.addExpense(new Expense(231.31f, "Żabka", payer, borrowers));
        expenseManager.addExpense(123.12f, "Biedronka", payer, borrowers);
        System.out.println("addExpense_isCorrect");
        System.out.println(expenseManager.toString());
    }


    @Test
    public void getExpenses_isCorrect() {
    }
}
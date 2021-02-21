package model;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExpenseTest {
    @Test

    public void additionExpense_isCorrect(){
        User payer = new User("1", "Bohdan");
        User borrower1 = new User("2", "Maciek");
        User borrower2 = new User("3", "Marcin");
        List<User> borrowers = new ArrayList<>();
        borrowers.add(borrower1);
        borrowers.add(borrower2);
        Expense expense = new Expense(3.14f, "Zakupy", payer, borrowers);
        assertNotNull(expense);
        System.out.println("additionExpense_isCorrect:");
        System.out.println(expense.toString());
    }

}
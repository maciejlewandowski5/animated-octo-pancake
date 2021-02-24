package model;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExpenseTest {
    @Test

    public void additionExpense_isCorrect(){
        User payer = new User("Bohdan");
        User borrower1 = new User("Maciek");
        User borrower2 = new User("Marcin");
        List<User> borrowers = new ArrayList<>();
        borrowers.add(borrower1);
        borrowers.add(borrower2);
        Expense expense = new Expense(3.14f, "Zakupy", payer, borrowers);
        assertNotNull(expense);
        System.out.println("additionExpense_isCorrect:");
        System.out.println(expense.toString());
    }

}
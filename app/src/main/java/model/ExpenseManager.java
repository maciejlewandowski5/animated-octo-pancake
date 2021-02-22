package model;

import java.util.ArrayList;

public class ExpenseManager {

    public ArrayList<Expense> getPayDebtExpenses(){
        ArrayList<Expense> expenses = new ArrayList<>();
        for(int i=0;i<3;i++){
            expenses.add(new Expense());
        }
        return expenses;
    }
}

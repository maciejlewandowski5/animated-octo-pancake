package model;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.mainactivity.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DebtManager {
    ArrayList<Debt> debts;
    ArrayList<User> mUsers;


    public DebtManager(DocumentSnapshot ds) {
        initializeDebts(ds);
    }


    private void initializeDebts(DocumentSnapshot ds) {
        debts = new ArrayList<>();
        mUsers = new ArrayList<>();
        Map<String, Object> borrowers = new HashMap<>();
        Map<String, Object> users = ((Map<String, Object>) ds.get("users"));
        for (Map.Entry<String, Object> payer : users.entrySet()) {
            mUsers.add(new User((String) payer.getValue(), payer.getKey()));
            borrowers = ((Map<String, Object>) ds.getData().get(payer.getKey()));
            for (Map.Entry<String, Object> borrower : borrowers.entrySet()) {
                try {
                    debts.add(new Debt(payer.getKey(), borrower.getKey(), ((Double) borrower.getValue()).floatValue()));
                } catch (ClassCastException e) {
                    debts.add(new Debt(payer.getKey(), borrower.getKey(), ((Long) borrower.getValue()).floatValue()));
                }
            }
        }
    }

    public void reduceDebts() {
        ArrayList<Integer> firstOfPair = new ArrayList<Integer>();
        ArrayList<Integer> secondOfPair = new ArrayList<Integer>();
        ArrayList<Debt> freshDebts = new ArrayList<>();

        //TODO::
        int i = 0;
        for (Debt debt1 : debts) {
            int j = 0;
            for (Debt debt2 : debts) {
                if (debt1.from.equals(debt2.to)) {
                    if (!firstOfPair.contains(i) && !secondOfPair.contains(j)) {
                        firstOfPair.add(i);
                        secondOfPair.add(j);
                    }
                }
                j++;
            }
            i++;
        }
        System.out.println("SIZE  " + firstOfPair.size() + "   " + secondOfPair.size() + "  " + debts.size());
        for (Integer k : firstOfPair) {
            try {
                freshDebts.addAll(Debt.mergeDebts(debts.get(secondOfPair.get(firstOfPair.indexOf(k))), debts.get(k)));
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        for (Integer k = 0; k < debts.size(); k++) {
            if (!firstOfPair.contains(k) && !secondOfPair.contains(k)) {
                freshDebts.add(debts.get(k));
            }
        }
        {
        debts = freshDebts;
        firstOfPair = new ArrayList<Integer>();
        secondOfPair = new ArrayList<Integer>();
        freshDebts = new ArrayList<>();
        i = 0;
        for (Debt debt1 : debts) {
            int j = 0;
            for (Debt debt2 : debts) {
                if(debt1.to.equals(debt2.to)&& debt2.from.equals(debt1.from)){
                    if(!firstOfPair.contains(i) && !secondOfPair.contains(j)) {
                        firstOfPair.add(debts.indexOf(debt1));
                        secondOfPair.add(debts.indexOf(debt2));
                    }
                }
            }
        }
        for(int d =0;d<debts.size();i++){
            try {
                freshDebts.add(new Debt(debts.get(firstOfPair.get(i)),debts.get(secondOfPair.get(i))));
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        }
        for (Integer k = 0; k < debts.size(); k++) {
            if (!firstOfPair.contains(k) && !secondOfPair.contains(k)) {
                freshDebts.add(debts.get(k));
            }
        }
        debts = freshDebts;
        freshDebts = new ArrayList<>();

        for (Debt debt : debts) {
            if (!debt.from.equals(debt.to) && debt.amount != 0) {
                freshDebts.add(debt);
            }
        }
        debts = freshDebts;
    }

    public void simplifyDebts() {
        ArrayList<Integer> firstOfPair = new ArrayList<Integer>();
        ArrayList<Integer> secondOfPair = new ArrayList<Integer>();
        ArrayList<Debt> freshDebts = new ArrayList<>();

        int i = 0;
        for (Debt debt1 : debts) {
            int j = 0;
            for (Debt debt2 : debts) {
                if (debt1.from.equals(debt2.to) && debt1.to.equals(debt2.from)) {
                    if (!firstOfPair.contains(i) && !secondOfPair.contains(j)) {
                        firstOfPair.add(i);
                        secondOfPair.add(j);
                    }
                    break;
                }
                j++;
            }
            i++;
        }
        for (Integer k : firstOfPair) {
            try {
                freshDebts.add(new Debt(debts.get(k), debts.get(secondOfPair.get(firstOfPair.indexOf(k)))));
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        for (Integer k = 0; k < debts.size(); k++) {
            if (!firstOfPair.contains(k) && !secondOfPair.contains(k)) {
                freshDebts.add(debts.get(k));
            }
        }
        debts = freshDebts;
        freshDebts = new ArrayList<>();

        for (Debt debt : debts) {
            if (!debt.from.equals(debt.to) && debt.amount != 0) {
                freshDebts.add(debt);
            }
        }
        debts = freshDebts;
    }

    @NonNull
    @Override
    public String toString() {
        String string = "";
        for (Debt debt : debts) {
            string += debts.indexOf(debt) + ". " + debt.toString() + "\n";
        }
        return string;
    }

    public DebtManager(ArrayList<Debt> debts) {
        this.debts = debts;
    }

    public ArrayList<Debt> getDebts() {
        return debts;
    }

    public void addDebts(ArrayList<Debt> debts) {
        this.debts.addAll(debts);
    }

    public void addDebt(Debt debt) {
        this.debts.add(debt);
    }

    public void setDebts(ArrayList<Debt> debts) {
        this.debts = debts;
    }

    private User getUser(String id) {
        for (User user : mUsers) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return new User("", "");
    }

    public ArrayList<Expense> getExpenses(Activity activity) {
        ArrayList<Expense> expenses = new ArrayList<>();
        for (Debt debt : debts) {
            expenses.add(new Expense(debt.getAmount(), activity.getString(R.string.payback), getUser(debt.getFrom()), new ArrayList<User>(Arrays.asList(getUser(debt.getTo())))));
        }
        return expenses;
    }
}

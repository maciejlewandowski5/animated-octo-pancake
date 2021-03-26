package modelv2;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Payer extends User implements Serializable {

    private ArrayList<User> borrowers;
    private ArrayList<Double> amounts;

    public Payer(String name, String id) {
        super(name, id);
        borrowers = new ArrayList<>();
        amounts = new ArrayList<>();
    }

    public Payer(User user) {
        super(user);
        borrowers = new ArrayList<>();
        amounts = new ArrayList<>();
    }

    public void addBorrower(User user, Double amount) {
        if (!borrowers.contains(user)) {
            borrowers.add(user);
            amounts.add(amount);
        } else {
            int index = borrowers.indexOf(user);
            double prv = amounts.get(index);
            amounts.set(index, prv + amount);
        }
    }

    public ArrayList<User> getBorrowers() {
        return borrowers;
    }

    public void removeBorrowedAmount(User user, double amount) {
        if (borrowers.contains(user)) {
            int index = borrowers.indexOf(user);
            double prv = amounts.get(index);
            amounts.set(index, prv - amount);
        }
    }

    Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        for (User borrower : borrowers) {
            result.put(borrower.getId(), amounts.get(borrowers.indexOf(borrower)));
        }
        return result;
    }

    public void clearDebts() {
        for (int i = 0; i < amounts.size(); i++) {
            amounts.set(i, 0d);
        }
    }

    public float getTotal() {
        float result = 0;
        for(Double amount : amounts){
            result += amount;
        }
        return  result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    public void removeBorrower(User currentUser) {
        borrowers.remove(currentUser);
    }
}

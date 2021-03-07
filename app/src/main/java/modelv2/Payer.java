package modelv2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Payer extends User {

    private ArrayList<User> borrowers;
    private ArrayList<Double> amounts;

    public Payer(String name, String id) {
        super(name, id);
        borrowers = new ArrayList<>();
        amounts = new ArrayList<>();
    }

    public Payer(User user) {
        super(user);
    }

    public void addBorrower(User user, Double amount) {
        if (!borrowers.contains(user.getId())) {
            borrowers.add(user);
            amounts.add(amount);
        } else {
            int index = borrowers.indexOf(user.getId());
            double prv = amounts.get(index);
            amounts.set(index, prv + amount);
        }
    }

    public ArrayList<User> getBorrowers() {
        return borrowers;
    }

    public void removeBorrower(User user, double amount) {
        if (borrowers.contains(user.getId())) {
            int index = borrowers.indexOf(user.getId());
            double prv = amounts.get(index);
            amounts.set(index, prv - amount);
        }
    }

    Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        borrowers.forEach(borrower -> {
            result.put(borrower.getId(),amounts.get(borrowers.indexOf(borrower)));
        });
        return result;
    }

    public void clearDebts() {
        for(int i=0;i<amounts.size();i++){
            amounts.set(i,0d);
        }
    }
}
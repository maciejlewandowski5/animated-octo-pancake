package modelv2;

import java.util.ArrayList;

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
        borrowers.add(user);
        amounts.add(amount);
    }

    public ArrayList<User> getBorrowers() {
        return borrowers;
    }
}

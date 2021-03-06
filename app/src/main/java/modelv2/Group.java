package modelv2;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Group {
    private String id;
    private String code;
    private String name;
    private ArrayList<User> users;
    private ArrayList<Payer> payers;

    public Group(DocumentSnapshot value) {
        id = value.getId();
        code = value.getString("code");
        name = value.getString("name");
        users = new ArrayList<>();
        payers = new ArrayList<>();

        try {
            Set<Map.Entry<String, Object>> usersEntrySet = ((Map<String, Object>) value.getData().get("users")).entrySet();


            usersEntrySet.forEach((entry) -> {
                User user = new User((String) entry.getValue(), entry.getKey());
                users.add(user);
                payers.add(new Payer(user));
            });

            payers.forEach((payer) -> {
                ((Map<String, Object>) value.getData().get(payer.getId())).entrySet().forEach((borrower) -> {
                    try {
                        payer.addBorrower(users.get(users.indexOf(borrower.getKey())), (Double) borrower.getValue());
                    }catch (ClassCastException){
                        payer.addBorrower(users.get(users.indexOf(borrower.getKey())), ((Long) borrower.getValue()).doubleValue());
                    }
                });
            });

        } catch (ClassCastException e) {
            throw e;
        }
    }

    public Group(String name, String code, User currentUser) {

    }

    public void addExpense(Expense expense) {
    }

    ;

    public float calculateTotal(DocumentSnapshot value, User currentUser) {
        float total = 0;
        for (Map.Entry<String, Object> borrower :
                ((Map<String, Object>) value.getData().get(currentUser.getId())).entrySet()) {
            try {
                total += ((Double) borrower.getValue()).floatValue();
            } catch (ClassCastException e) {
                total += ((Long) borrower.getValue()).floatValue();
            }
        }
        return total;
    }

    public float calculateAbsoluteTotal(DocumentSnapshot value) {
        float absoluteTotal = 0;
        for (Map.Entry<String, Object> user : ((Map<String, Object>) value.getData().get("users")).entrySet()) {
            for (Map.Entry<String, Object> borrower : ((Map<String, Object>) value.getData().get(user.getKey())).entrySet()) {
                try {
                    absoluteTotal += ((Double) borrower.getValue()).floatValue();
                } catch (ClassCastException e) {
                    absoluteTotal += ((Long) borrower.getValue()).floatValue();
                }
            }

        }
        return absoluteTotal;
    }

    public void clearExpenses() {
    }

    public ArrayList<Expense> getExpenses() {
        return new ArrayList<Expense>();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            return this.id.equals((String) obj);
        } catch (ClassCastException e) {
            try {
                return this.id.equals(((Group) obj).getId());
            } catch (ClassCastException f) {
                return false;
            }
        }
    }


    Map<String, Object> toMap() {
        //TODO::
        return new HashMap<>();
    }

    public void addUser(User currentUser) {
    }

    public ShallowGroup shallowValue() {
        return new ShallowGroup("", "");
    }

    public void editExpense(Expense expense) {
    }
}

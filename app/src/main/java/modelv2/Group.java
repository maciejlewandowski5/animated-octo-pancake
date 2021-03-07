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
    private ArrayList<Expense> expenses;

    public Group(DocumentSnapshot value) {
        id = value.getId();
        code = value.getString("code");
        name = value.getString("name");
        users = new ArrayList<>();
        payers = new ArrayList<>();
        expenses = new ArrayList<>();

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
                    } catch (ClassCastException e) {
                        payer.addBorrower(users.get(users.indexOf(borrower.getKey())), ((Long) borrower.getValue()).doubleValue());
                    }
                });
            });

        } catch (ClassCastException e) {
            throw e;
        }
    }

    public Group(String name, String code, User currentUser) {
        id = null;
        this.code = code;
        this.name = name;
        users = new ArrayList<>();
        payers = new ArrayList<>();
        expenses = new ArrayList<>();

        users.add(currentUser);
        payers.add(new Payer(currentUser));
    }

    //also can edit expenses
    public void addExpense(Expense expense) {
        if (expenses.contains(expense)) {
            Expense tmp = expenses.get(expenses.indexOf(expense));
            int index = payers.indexOf(tmp.getPayer().getId());
            if (index > -1) {
                for (User borrower : tmp.getBorrowers()) {
                    payers.get(index).removeBorrower(borrower, tmp.getAmount() / (double) tmp.getBorrowers().size());
                }
            } else {
                throw new IllegalArgumentException("No such payer in group");
            }
            expenses.remove(tmp);
        }
        expenses.add(expense);
        int index = payers.indexOf(expense.getPayer().getId());
        if (index > -1) {
            for (User borrower : expense.getBorrowers()) {
                payers.get(index).addBorrower(borrower, expense.getAmount() / (double) expense.getBorrowers().size());
            }
        } else {
            throw new IllegalArgumentException("No such payer in group");
        }
    }



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
        expenses.clear();
        payers.forEach(Payer::clearDebts);
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
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
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("name", name);
        Map<String, Object> nested = new HashMap<>();
        users.forEach(user -> {
            nested.put(user.getId(), user.getName());
        });
        result.put("users", nested);
        payers.forEach(payer -> {
            result.put(payer.getId(), payer.toMap());
        });
        return result;
    }

    public void addUser(User user) {
        users.add(user);
        payers.add(new Payer(user));
    }

    public ShallowGroup shallowValue() throws InstantiationException {
        if (id != null) {
            return new ShallowGroup(id, name);
        }
        else throw new InstantiationException("Id in Group is null");
    }

}

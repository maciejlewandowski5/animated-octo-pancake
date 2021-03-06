package modelv2;

import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class Expense {
    private String id;
    private String name;
    private float amount;
    private Date dateTime;
    private User payer;
    private ArrayList<User> borrowers;

    public Expense(DocumentSnapshot ds) {
        id = ds.getId();
        amount  = Objects.requireNonNull(ds.getDouble("amount")).floatValue();
        dateTime = ds.getDate("dateTime");
        name = ds.getString("name");
        payer = new User(ds.getString("payerName"),ds.getString("payer"));
        borrowers = new ArrayList<>();
        try {
            Set<Map.Entry<String, Object>> tmp = ((Map<String, Object>) Objects.requireNonNull(ds.get("borrowers"))).entrySet();
            for(Map.Entry<String,Object> borrower : tmp){
                borrowers.add(new User((String) borrower.getValue(),borrower.getKey()));
            }
        }catch (ClassCastException e){
            throw e;
        }

    }

    public Expense(String name, float amount, Date dateTime, User payer, ArrayList<User> borrowers) {
        this.name = name;
        this.amount = amount;
        this.dateTime = dateTime;
        this.payer = payer;
        this.borrowers = borrowers;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }

    public void setBorrowers(ArrayList<User> borrowers) {
        this.borrowers = borrowers;
    }

    Map<String,Object> toMap() {
        Map<String,Object> result = new HashMap<>();
        result.put("amount",amount);
        result.put("dateTime",dateTime);
        result.put("name",name);
        result.put("payer",payer.getId());
        result.put("payerName",payer.getName());
        Map<String,Object> nested = new HashMap<>();
        borrowers.forEach((user) -> {
            nested.put(user.getId(),user.getName());
        });
        result.put("borrowers",nested);
        return result;
    }

    public String getId() {
        return  id;
    }
}

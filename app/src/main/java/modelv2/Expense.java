package modelv2;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class Expense implements Serializable {
    private String id;
    private String name;
    private Double amount;
    private Date dateTime;
    private User payer;
    private ArrayList<User> borrowers;

    public Expense(DocumentSnapshot ds) {
        id = ds.getId();
        amount  = Objects.requireNonNull(ds.getDouble("amount"));
        dateTime = ds.getDate("dateTime");
        name = ds.getString("name");
        payer = new User(ds.getString("payerName"),ds.getString("payer"));
        borrowers = new ArrayList<>();
        try {
            Set<Map.Entry<String, Object>> tmp = ((Map<String, Object>)
                    Objects.requireNonNull(ds.get("borrowers"))).entrySet();
            for(Map.Entry<String,Object> borrower : tmp){
                borrowers.add(new User((String) borrower.getValue(),borrower.getKey()));
            }
        }catch (ClassCastException e){
            throw e;
        }

    }

    public Expense(String name, Double amount, Date dateTime, User payer, ArrayList<User> borrowers) {
        this.name = name;
        this.amount = amount;
        this.dateTime = dateTime;
        this.payer = payer;
        this.borrowers = borrowers;
    }


    public User getPayer() {
        return payer;
    }


    public String getName() {
        return name;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public ArrayList<User> getBorrowers() {
        return borrowers;
    }

    public Map<String,Object> toMap() {
        Map<String,Object> result = new HashMap<>();
        result.put("amount",amount);
        result.put("dateTime",dateTime);
        result.put("name",name);
        result.put("payer",payer.getId());
        result.put("payerName",payer.getName());
        Map<String,Object> nested = new HashMap<>();
        for (User user : borrowers) {
            nested.put(user.getId(), user.getName());
        }
        result.put("borrowers",nested);
        return result;
    }

    public String getId() {
        return  id;
    }

    public Double getAmount() {
        return amount;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(id==null){
            return false;
        }
        try {
            return this.id.equals((String) obj);
        } catch (ClassCastException e) {
            try {
                return this.id.equals(((Expense) obj).getId());
            } catch (ClassCastException f) {
                return false;
            }
        }
    }

    public void setId(String id) {
    this.id = id;
    }
}

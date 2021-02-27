package model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Expense implements Serializable {

    private User payer;
    private List<User> borrowers;
    private float amount;
    private Date dateTime;
    private String id;
    private String name;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> nested = new HashMap<>();
        for (User borrower : borrowers) {
            nested.put(borrower.getId(),borrower.getName());
        }
        result.put("borrowers",nested);
        result.put("amount",amount);
        result.put("dateTime",dateTime);
        result.put("payer",payer.getId());
        result.put("payerName",payer.getName());
        result.put("name",name);
        return result;
    }

    public Expense(float amount, String name, User payer, List<User> borrowers) {
        this.amount = amount;
        this.dateTime = new Date(System.currentTimeMillis());
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.payer = payer;
        this.borrowers = borrowers;
    }
    public Expense(DocumentSnapshot ds){
        id = ds.getId();
        amount  = ds.getDouble("amount").floatValue();
        dateTime = ds.getDate("dateTime");
        name = ds.getString("name");
        payer = new User(ds.getString("payerName"),ds.getString("payer"));
        payer.setId(ds.getString("payer"));
        borrowers = new ArrayList<>();
        //TODO::
        Set<Map.Entry<String,Object>> a = ((Map<String,Object>)ds.get("borrowers")).entrySet();
       for(Map.Entry<String,Object> e : a){
            borrowers.add(new User((String) e.getValue(),e.getKey()));
        }

    }
    @Exclude
    public String getId() {
        return id;
    }

    public User getPayer() {
        return payer;
    }

    public float getAmount(){
        return amount;
    }

    public String getName() {
        return name;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public List<User> getBorrowers() {
        return borrowers;
    }

    public int getNumberOfBorrowers(){
        return borrowers.size();
    }

    @Override
    public String toString() {
        return "Expense{" +
                "payer=" + payer +
                ", borrowers=" + borrowers +
                ", amount=" + amount +
                ", dateTime=" + dateTime +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}' + "\n";
    }


}

package model;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;


import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Expense implements Serializable {

    private User payer;
    private List<User> borrowers;
    private float amount;
    private Date dateTime;
    @DocumentId
    private String id;
    private String name;

    public Expense(float amount, String name, User payer, List<User> borrowers) {
        this.amount = amount;
        this.dateTime = new Date(System.currentTimeMillis());
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.payer = payer;
        this.borrowers = borrowers;
    }

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

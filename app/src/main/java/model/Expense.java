package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Expense implements Serializable {

    private User payer;
    private List<User> borrowers;
    private float amount;
    private Date dateTime;
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

    public User getPayer() {
        return payer;
    }

    public float getAmount(){
        return amount;
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

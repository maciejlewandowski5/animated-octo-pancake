package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Expense {

    User payer;
    List<User> borrowers;
    float amount;
    Timestamp dateTime;
    String id;
    String name;

    public Expense(float amount, String name, User payer, List<User> borrowers) {
        this.amount = amount;
        this.dateTime = new Timestamp(System.currentTimeMillis());
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.payer = payer;
        this.borrowers = borrowers;
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

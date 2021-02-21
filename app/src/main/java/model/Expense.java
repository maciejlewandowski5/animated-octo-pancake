package model;

import java.io.Serializable;
import java.util.Date;

public class Expense implements Serializable {

    public String getTitle(){return "title";}
    public String getUser(){return "user";}
    public Date getDate(){return new Date();}
    public float getAmount(){return 10.58f;}

}

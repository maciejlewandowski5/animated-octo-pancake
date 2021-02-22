package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {

    public String getName() {
        return "Title";
    }

    public String getCode() {
        return "code";
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> result = new ArrayList<User>();

        for (Integer i = 0; i < 10; i++) {
            result.add(new User(i.toString()));
        }
        return result;
    }

    public User getCurrentUser() {
        return new User("CurrentUser");
    }

    public float getMeanAmount() {
        return 15;
    }

    public float getMaxAmount() { // średnia plus odchylenie standardowe
        return 57.78f;
    }

    public float getMinAmount() { // średnia minus odchylenie standardowe
        return 57.78f;
    }

    public ExpenseManager getExpenseManager(){
        return new ExpenseManager();
    }
}

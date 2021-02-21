package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group implements Serializable {

    private List<User> users;
    private ExpenseManager expenseManager;
    private String id;
    private String code;
    private String name;

    public Group(String code, String name) {
      this.users = new ArrayList<>();
      this.expenseManager = new ExpenseManager();
      this.id = UUID.randomUUID().toString();
      this.code = code;
      this.name = name;
    }

    public void addUser(User user){
            users.add(user);
    }


}

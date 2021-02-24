package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group implements Serializable {

    private List<User> users;
    private User currentUser;
    private ExpenseManager expenseManager;
    private String id;
    private String code;
    private String name;

    public Group(String code, String name, User user) {
      this.users = new ArrayList<>();
      this.expenseManager = new ExpenseManager();
      this.id = UUID.randomUUID().toString();
      this.code = code;
      this.name = name;
      currentUser = user;
    }

    public float getCurrentUsersBalance(){
        return expenseManager.getUserBalance(currentUser);
    }

    public int getCurrentUserPercentBalanceInGroupTotalBalance(){
        return expenseManager.getUserPercentBalanceInExpensesTotalBalance(currentUser);
    }

    public List<Expense> getCurrentUserSuggestedPayDebtExpenses(){
        return expenseManager.getUserPayDebtExpenses(currentUser, users);
    }


    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }

    public void addUser(User user){
            users.add(user);
    }


}

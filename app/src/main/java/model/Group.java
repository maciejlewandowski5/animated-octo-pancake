package model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Group implements Serializable {

    private List<User> users;
    private User currentUser;
    private ExpenseManager expenseManager;
    @DocumentId
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

    public Group() {

    }

    @Exclude
    public float getCurrentUsersBalance() {
        return expenseManager.getUserBalance(currentUser);
    }

    @Exclude
    public int getCurrentUserPercentBalanceInGroupTotalBalance() {
        return expenseManager.getUserPercentBalanceInExpensesTotalBalance(currentUser);
    }

    @Exclude
    public List<Expense> getCurrentUserSuggestedPayDebtExpenses() {
        return expenseManager.getUserPayDebtExpenses(currentUser, users);
    }
    public void setNumberOfExpenses(Long numberOfExpenses){

    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", this.code);
        result.put("name", this.name);
        result.put("numberOfExpenses", this.getExpenseManager().getExpenses().size());
        result.put("totalBallance", this.getExpenseManager().getTotalBallance());
        result.put("xMinMeanSq",this.getXMinMeanSq());
        for (User user : this.users) {
            Map<String, Object> nested = new HashMap<>();
            for (User borrower : this.users) {
                if (!user.equals(borrower)) {
                    nested.put(borrower.getId(), this.getTotalUserDebt(borrower));
                }
            }
            result.put(user.getId(), nested);
        }
        Map<String, Object> nested = new HashMap<>();
        for(User user : users){
            nested.put(user.getId(),user.getName());
        }
        result.put("usersBallance",nested);
        return result;
    }

    //TODO::
    public float getXMinMeanSq(){
        return 50;
    }

    //TODO::
    public float getTotalUserDebt(User user) {
        return 50;
    }

    //TODO::
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public ExpenseManager getExpenseManager() {
        return expenseManager;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    //TODO::
    public float getMeanAmount() {
        return 15;
    }

    //TODO::
    public float getMaxAmount() { // średnia plus odchylenie standardowe
        return 57.78f;
    }

    //TODO::
    public float getMinAmount() { // średnia minus odchylenie standardowe
        return 57.78f;
    }

    public String getId() {
        return id;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setExpenseManager(ExpenseManager expenseManager) {
        this.expenseManager = expenseManager;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalBallance(Double totalBallance) {
    }

    //TODO::
    public void addUserDebt(float a) {
    }

    public static Group fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        Group group = new Group();
        group.setId(documentSnapshot.getId());
        group.setCode(documentSnapshot.getString("code"));
        group.setName(documentSnapshot.getString("name"));
        group.setNumberOfExpenses(documentSnapshot.getLong("numberOfExpenses"));
        group.setTotalBallance(documentSnapshot.getDouble("totalBallance"));
        Set<String> usersIds = ((Map<String, Object>) documentSnapshot.getData().get("userBallance")).keySet();
        for (String userId : usersIds) {
            group.addUser(new User(userId));
        }
        for (User user : group.getUsers()) {
            if (documentSnapshot.contains(user.getId())) {
                Map<String, Object> debtMap = ((Map<String, Object>) documentSnapshot.getData().get(user.getId()));

                for (Map.Entry<String, Object> entry : debtMap.entrySet()) {
                    group.setUserDebt(user,entry.getKey(),(float)entry.getValue());
                }
            }
        }
        return group;
    }

    public void setUserDebt(User user,String borrower,float a) {
    }
}

package model;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupManager implements Serializable {

    private static GroupManager singleInstance = null;
    private Group currentGroup;
    private List<Group> groups;

    private GroupManager() {
        this.groups = new ArrayList<>();
    }

    public void addGroup(String code, String name, User user){
        Group group = new Group(code, name, user);
        groups.add(group);
    }
    public void addGroup(DocumentSnapshot documentSnapshot){
        groups.add(GroupManager.fromDocumentSnapshot(documentSnapshot));
    }
    public void addCurrentGroup(DocumentSnapshot documentSnapshot){
        Group group = GroupManager.fromDocumentSnapshot(documentSnapshot);
        currentGroup = group;
        groups.add(group);
    }
    public void clearGroups(){
        groups = new ArrayList<>();
        currentGroup = null;
    }


    private static Group fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        Group group = new Group();
        group.setId(documentSnapshot.getId());
        group.setCode(documentSnapshot.getString("code"));
        group.setName(documentSnapshot.getString("name"));
        group.setNumberOfExpenses(documentSnapshot.getLong("numberOfExpenses"));
        group.setTotalBallance(documentSnapshot.getDouble("totalBallance"));
        documentSnapshot.getData().get("users");
       Set<Map.Entry<String,Object>> usersIds = ((Map<String, Object>) documentSnapshot.getData().get("users")).entrySet();
        for (Map.Entry<String,Object> user : usersIds) {
            group.addUser(new User((String)user.getValue(),user.getKey()));
        }
        for (User user : group.getUsers()) {
            if (documentSnapshot.contains(user.getId())) {
                Map<String, Object> debtMap = ((Map<String, Object>) documentSnapshot.getData().get(user.getId()));

                for (Map.Entry<String, Object> entry : debtMap.entrySet()) {
                    try {
                        group.setUserDebt(user, entry.getKey(), ((Double) entry.getValue()).floatValue());
                    }catch (ClassCastException e){
                        group.setUserDebt(user, entry.getKey(), ((Long) entry.getValue()).floatValue());
                    }
                    }
            }
        }
        return group;
    }

    public static GroupManager getInstance(){
        if (singleInstance == null){
            singleInstance = new GroupManager();
        }
        return singleInstance;
    }

    public List<Group> getGroups(){
        return this.groups;
    }

    public Group getCurrentGroup(){

        return currentGroup;
    }

    public void setCurrentGroup(Group group){
        currentGroup = group;
    }


}

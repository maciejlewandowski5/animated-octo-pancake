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
        groups.add(new Group(code, name, user));
    }
    public void addGroup(DocumentSnapshot documentSnapshot){
        groups.add(GroupManager.fromDocumentSnapshot(documentSnapshot));
    }

    private static Group fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
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

        if(currentGroup == null){
            return groups.get(0);
        }
        return currentGroup;
    }

    public void setCurrentGroup(Group group){
        currentGroup = group;
    }


}

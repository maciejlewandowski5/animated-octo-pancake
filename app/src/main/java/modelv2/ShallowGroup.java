package modelv2;

import java.util.HashMap;
import java.util.Map;

public class ShallowGroup {
    String groupId;
    String groupName;

    public ShallowGroup(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public ShallowGroup(Map<String, Object> currentGroup) {

        currentGroup.forEach((k, v) -> {
            groupId = k;
            groupName = (String) v;
        });

    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put(groupId,groupName);
        return result;
    }
}

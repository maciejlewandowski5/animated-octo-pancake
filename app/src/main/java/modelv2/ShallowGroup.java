package modelv2;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ShallowGroup implements Serializable {
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

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            return this.groupId.equals(((Group) obj).getId());
        } catch (ClassCastException e) {
            try {
                return this.groupId.equals(((ShallowGroup) obj).getGroupId());
            } catch (ClassCastException f) {
                return false;
            }
        }
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

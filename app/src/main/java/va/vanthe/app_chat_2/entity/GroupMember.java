package va.vanthe.app_chat_2.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@Entity
public class GroupMember implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;
    private String userId;
    private String conversationId;
    private Date timeJoin;
    private int status;

    public GroupMember() {}

    public GroupMember(String id, String userId, String conversationId, Date timeJoin, int status) {
        this.id = id;
        this.userId = userId;
        this.conversationId = conversationId;
        this.timeJoin = timeJoin;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Date getTimeJoin() {
        return timeJoin;
    }

    public void setTimeJoin(Date timeJoin) {
        this.timeJoin = timeJoin;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", timeJoin=" + timeJoin +
                ", status=" + status +
                '}';
    }
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> groupMemberMap = new HashMap<>();
        groupMemberMap.put("userId", userId);
        groupMemberMap.put("conversationId", conversationId);
        groupMemberMap.put("timeJoin", timeJoin);
        groupMemberMap.put("status", status);
        return groupMemberMap;
    }
}

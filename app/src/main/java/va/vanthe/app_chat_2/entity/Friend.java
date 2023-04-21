package va.vanthe.app_chat_2.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.HashMap;

@Entity
public class Friend {
    @PrimaryKey
    @NonNull
    private String id;
    private String userId;
    private String userFriendId;
    private int status;
    private Date timeStamp;



    public Friend(@NonNull String id, String userId, String userFriendId, int status, Date timeStamp) {
        this.id = id;
        this.userId = userId;
        this.userFriendId = userFriendId;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFriendId() {
        return userFriendId;
    }

    public void setUserFriendId(String userFriendId) {
        this.userFriendId = userFriendId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> friendMap = new HashMap<>();
        friendMap.put("friendId", id);
        friendMap.put("userId", userId);
        friendMap.put("userFriendId", userFriendId);
        friendMap.put("status", status);
        friendMap.put("timeStamp", timeStamp);
        return friendMap;
    }
}

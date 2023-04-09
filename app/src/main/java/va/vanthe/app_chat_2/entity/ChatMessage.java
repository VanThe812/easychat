package va.vanthe.app_chat_2.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.HashMap;

@Entity
public class ChatMessage {

    @PrimaryKey
    @NonNull
    private String id;
    private String senderId;
    private String message;
    private Date dataTime;
    private String conversationId;
    private int styleMessage;

    public ChatMessage() {}

    public ChatMessage(String id, String senderId, String message, Date dataTime, String conversationId, int styleMessage) {
        this.id = id;
        this.senderId = senderId;
        this.message = message;
        this.dataTime = dataTime;
        this.conversationId = conversationId;
        this.styleMessage = styleMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getStyleMessage() {
        return styleMessage;
    }

    public void setStyleMessage(int styleMessage) {
        this.styleMessage = styleMessage;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> chatMessageMap = new HashMap<>();
        chatMessageMap.put("senderId", senderId);
        chatMessageMap.put("message", message);
        chatMessageMap.put("dataTime", dataTime);
        chatMessageMap.put("conversationId", conversationId);
        chatMessageMap.put("styleMessage", styleMessage);
        return chatMessageMap;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", message='" + message + '\'' +
                ", dataTime=" + dataTime +
                ", conversationId='" + conversationId + '\'' +
                ", styleMessage=" + styleMessage +
                '}';
    }
}

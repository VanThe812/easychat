package va.vanthe.app_chat_2.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;


@Entity
public class Conversation implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;
    private Date createTime;
    private String newMessage;
    private String senderId;
    private Date messageTime;
    private int styleChat;

    public Conversation() {}
    public Conversation(String id, Date createTime, String newMessage, String senderId, Date messageTime, int styleChat) {
        this.id = id;
        this.createTime = createTime;
        this.newMessage = newMessage;
        this.senderId = senderId;
        this.messageTime = messageTime;
        this.styleChat = styleChat;
    }

    public String getId() {
        return id;
    }

    public void setId(String conversationId) {
        this.id = conversationId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    public int getStyleChat() {
        return styleChat;
    }

    public void setStyleChat(int styleChat) {
        this.styleChat = styleChat;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> conversationMap = new HashMap<>();
        conversationMap.put("createTime", createTime);
        conversationMap.put("newMessage", newMessage);
        conversationMap.put("senderId", senderId);
        conversationMap.put("messageTime", messageTime);
        conversationMap.put("styleChat", styleChat);
        return conversationMap;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id='" + id + '\'' +
                ", createTime=" + createTime +
                ", newMessage='" + newMessage + '\'' +
                ", senderId='" + senderId + '\'' +
                ", messageTime=" + messageTime +
                ", styleChat=" + styleChat +
                '}';
    }
}

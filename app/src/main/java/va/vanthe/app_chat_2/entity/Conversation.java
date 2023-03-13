package va.vanthe.app_chat_2.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity
public class Conversation {

    @PrimaryKey
    @NonNull
    private String id;
    private Date createTime;
    private String newMessage;
    private String senderId;
    private Date messageTime;

    public Conversation(String id, Date createTime, String newMessage, String senderId, Date messageTime) {
        this.id = id;
        this.createTime = createTime;
        this.newMessage = newMessage;
        this.senderId = senderId;
        this.messageTime = messageTime;
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
}

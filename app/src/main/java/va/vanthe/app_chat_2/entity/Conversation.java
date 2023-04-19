package va.vanthe.app_chat_2.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


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
    private String conversationName;
    private String conversationBackground;
    private String conversationColor;
    private String conversationAvatar;
    private String quickEmotions;

    public Conversation() {}

    public Conversation(@NonNull String id, Date createTime, String newMessage, String senderId, Date messageTime, int styleChat, String conversationName, String conversationBackground, String conversationColor, String conversationAvatar, String quickEmotions) {
        this.id = id;
        this.createTime = createTime;
        this.newMessage = newMessage;
        this.senderId = senderId;
        this.messageTime = messageTime;
        this.styleChat = styleChat;
        this.conversationName = conversationName;
        this.conversationBackground = conversationBackground;
        this.conversationColor = conversationColor;
        this.conversationAvatar = conversationAvatar;
        this.quickEmotions = quickEmotions;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
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

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getConversationBackground() {
        return conversationBackground;
    }

    public void setConversationBackground(String conversationBackground) {
        this.conversationBackground = conversationBackground;
    }

    public String getConversationColor() {
        return conversationColor;
    }

    public void setConversationColor(String conversationColor) {
        this.conversationColor = conversationColor;
    }

    public String getConversationAvatar() {
        return conversationAvatar;
    }

    public void setConversationAvatar(String conversationAvatar) {
        this.conversationAvatar = conversationAvatar;
    }

    public String getQuickEmotions() {
        return quickEmotions;
    }

    public void setQuickEmotions(String quickEmotions) {
        this.quickEmotions = quickEmotions;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> conversationMap = new HashMap<>();
        conversationMap.put("conversationId", id);
        conversationMap.put("createTime", createTime);
        conversationMap.put("newMessage", newMessage);
        conversationMap.put("senderId", senderId);
        conversationMap.put("messageTime", messageTime);
        conversationMap.put("styleChat", styleChat);
        conversationMap.put("conversationName", conversationName);
        conversationMap.put("conversationBackground", conversationBackground);
        conversationMap.put("conversationColor", conversationColor);
        conversationMap.put("conversationAvatar", conversationAvatar);
        conversationMap.put("quickEmotions", quickEmotions);
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
                ", conversationName='" + conversationName + '\'' +
                ", conversationBackground='" + conversationBackground + '\'' +
                ", conversationColor='" + conversationColor + '\'' +
                ", conversationAvatar='" + conversationAvatar + '\'' +
                ", quickEmotions='" + quickEmotions + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

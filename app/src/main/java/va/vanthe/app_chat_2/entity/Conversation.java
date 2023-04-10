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
    private String backgroundImage;
    private String quickEmotions;
    private int theme;

    public Conversation() {}

    public Conversation(@NonNull String id, Date createTime, String newMessage, String senderId, Date messageTime, int styleChat, String conversationName, String backgroundImage, String quickEmotions, int theme) {
        this.id = id;
        this.createTime = createTime;
        this.newMessage = newMessage;
        this.senderId = senderId;
        this.messageTime = messageTime;
        this.styleChat = styleChat;
        this.conversationName = conversationName;
        this.backgroundImage = backgroundImage;
        this.quickEmotions = quickEmotions;
        this.theme = theme;
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

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getQuickEmotions() {
        return quickEmotions;
    }

    public void setQuickEmotions(String quickEmotions) {
        this.quickEmotions = quickEmotions;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
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
        conversationMap.put("backgroundImage", backgroundImage);
        conversationMap.put("quickEmotions", quickEmotions);
        conversationMap.put("theme", theme);
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
                ", conversationName=" + conversationName +
                ", quickEmotions=" + quickEmotions +
                ", theme=" + theme +
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

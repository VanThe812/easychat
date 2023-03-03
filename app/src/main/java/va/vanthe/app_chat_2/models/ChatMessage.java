package va.vanthe.app_chat_2.models;

import java.util.Date;

public class ChatMessage {
    public String senderId, receiverId, message, datatime;
    public String senderName, senderNickname, senderImage;
    public Date dateObject;
    public String conversionId, conversionName, conversionImage;
    public String conversationId, receiverNickname;

    public String statusMessage;
}
// conversation:
//- id, type (1:chat/2:group), time, avatar, newMessage, senderId,
//

// singleChat
// id, avatar, newMessage, listUserId,
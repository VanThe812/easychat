package va.vanthe.app_chat_2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import va.vanthe.app_chat_2.entity.ChatMessage;

@Dao
public interface ChatMessageDAO {
    @Insert
    void insertChatMessage(ChatMessage chatMessage);

    @Query("SELECT * FROM ChatMessage WHERE conversationId = :conversationId ")
    List<ChatMessage> getChatMessage(String conversationId);

    @Query("SELECT COUNT(*) FROM ChatMessage WHERE id = :chatMessageId")
    int checkChatMessage(String chatMessageId);
}

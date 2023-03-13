package va.vanthe.app_chat_2.database;

import androidx.room.Dao;
import androidx.room.Insert;

import va.vanthe.app_chat_2.entity.ChatMessage;

@Dao
public interface ChatMessageDAO {
    @Insert
    void insertChatMessage(ChatMessage chatMessage);

}

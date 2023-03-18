package va.vanthe.app_chat_2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import va.vanthe.app_chat_2.entity.Conversation;

@Dao
public interface ConversationDAO {

    @Insert
    void insertConversation(Conversation conversation);

    @Query("select * from Conversation")
    List<Conversation> getConversation();

    @Query("SELECT * FROM Conversation WHERE id = :conversationId ORDER BY messageTime ASC")
    Conversation getOneConversation(String conversationId);
}

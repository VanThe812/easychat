package va.vanthe.app_chat_2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import va.vanthe.app_chat_2.entity.Conversation;

@Dao
public interface ConversationDAO {

    @Insert
    void insertConversation(Conversation conversation);

    @Update
    void updateConversation(Conversation conversation);

    @Query("select * from Conversation")
    List<Conversation> getConversation();

    @Query("select * from Conversation where styleChat = :styleChat")
    List<Conversation> getConversationStyleChat(int styleChat);

    @Query("SELECT * FROM Conversation WHERE id = :conversationId ORDER BY messageTime ASC")
    Conversation getOneConversation(String conversationId);

    @Query("SELECT COUNT(*) FROM Conversation WHERE id = :conversationId")
    int checkConversation(String conversationId);
}

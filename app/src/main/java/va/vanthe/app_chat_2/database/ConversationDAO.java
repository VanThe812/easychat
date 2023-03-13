package va.vanthe.app_chat_2.database;

import androidx.room.Dao;
import androidx.room.Insert;

import va.vanthe.app_chat_2.entity.Conversation;

@Dao
public interface ConversationDAO {

    @Insert
    void insertConversation(Conversation conversation);

}

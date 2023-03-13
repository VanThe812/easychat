package va.vanthe.app_chat_2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.Converters;

@Database(entities = {Conversation.class}, version = Constants.KEY_VERSION_ROOM)
@TypeConverters({Converters.class})
public abstract class ConversationDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "conversation.db";
    private static ConversationDatabase instance;

    public static synchronized ConversationDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ConversationDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract ConversationDAO conversationDAO();
}

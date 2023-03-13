package va.vanthe.app_chat_2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import va.vanthe.app_chat_2.entity.ChatMessage;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.Converters;

@Database(entities = {ChatMessage.class}, version = Constants.KEY_VERSION_ROOM)
@TypeConverters({Converters.class})
public abstract class ChatMessageDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "chatmessage.db";
    private static ChatMessageDatabase instance;

    public static synchronized ChatMessageDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ChatMessageDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract ChatMessageDAO chatMessageDAO();
}

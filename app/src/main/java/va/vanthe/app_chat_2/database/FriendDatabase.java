package va.vanthe.app_chat_2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import va.vanthe.app_chat_2.entity.Friend;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.Converters;

@Database(entities = {Friend.class}, version = Constants.KEY_VERSION_ROOM)
@TypeConverters({Converters.class})
public abstract class FriendDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "friend.db";
    private static FriendDatabase instance;

    public static synchronized FriendDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), FriendDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract FriendDAO friendDAO();
}

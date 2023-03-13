package va.vanthe.app_chat_2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.Converters;

@Database(entities = {GroupMember.class}, version = Constants.KEY_VERSION_ROOM)
@TypeConverters({Converters.class})
public abstract class GroupMemberDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "groupmember.db";
    private static GroupMemberDatabase instance;

    public static synchronized GroupMemberDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), GroupMemberDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract GroupMemberDAO groupMemberDAO();
}

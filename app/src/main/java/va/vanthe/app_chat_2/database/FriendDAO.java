package va.vanthe.app_chat_2.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import va.vanthe.app_chat_2.entity.Friend;
import va.vanthe.app_chat_2.entity.User;

@Dao
public interface FriendDAO {

    @Insert
    void insertFriend(Friend friend);

    @Update
    void updateFriend(Friend friend);

    @Delete
    void deleteFriend(Friend friend);

    @Query("SELECT * FROM friend")
    List<Friend> getAllFriend();

    @Query("SELECT * FROM friend WHERE id = :friendId")
    Friend getFriend(String friendId);

//    @Query("SELECT COUNT(*) FROM User WHERE id = :userId")
//    int checkUser(String userId);

}

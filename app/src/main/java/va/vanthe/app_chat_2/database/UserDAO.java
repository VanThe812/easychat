package va.vanthe.app_chat_2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import va.vanthe.app_chat_2.entity.User;

@Dao
public interface UserDAO {

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM user")
    List<User> getListUser();

//    @Query("SELECT * FROM user WHERE phoneNUmber= :phoneNumber")
//    List<User> checkUser(String phoneNumber);

    @Query("SELECT * FROM user WHERE id = :userId")
    User getUser(String userId);

    @Query("SELECT COUNT(*) FROM User WHERE id = :userId")
    int checkUser(String userId);

}

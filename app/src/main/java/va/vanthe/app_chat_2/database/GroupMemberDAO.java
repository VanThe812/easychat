package va.vanthe.app_chat_2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;

@Dao
public interface GroupMemberDAO {

    @Insert
    void insertGroupMember(GroupMember groupMember);

    @Query("SELECT * FROM groupmember WHERE userId = :userId AND status = 1")
    GroupMember hasTextedUser(String userId);
}

package va.vanthe.app_chat_2.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;

@Dao
public interface GroupMemberDAO {

    @Query("DELETE FROM GroupMember")
    void deleteAllGroupMember();

    @Insert
    void insertGroupMember(GroupMember groupMember);

    @Query("SELECT * FROM groupmember WHERE userId = :userId AND status = 1")
    GroupMember hasTextedUser(String userId);

    @Query("SELECT * FROM groupmember where userId != :userId AND conversationId = :conversationId")
    GroupMember getGroupMember(String userId, String conversationId);

    @Query("SELECT * FROM groupmember where userId != :userId AND conversationId = :conversationId")
    List<GroupMember> getListGroupMember(String userId, String conversationId);

    @Query("SELECT * FROM groupmember where userId == :userId AND conversationId = :conversationId")
    GroupMember getGroupMember2(String userId, String conversationId);

//    @Query("SELECT * FROM groupmember where conversationId = :conversationId")
//    GroupMember getGroupMember2(String conversationId);

    @Query("SELECT COUNT(*) FROM GroupMember WHERE id = :groupMemberId")
    int checkGroupMember(String groupMemberId);
}

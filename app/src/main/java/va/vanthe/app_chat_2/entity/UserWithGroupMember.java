package va.vanthe.app_chat_2.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class UserWithGroupMember {
    @Embedded public GroupMember groupMember;

    @Relation(
            parentColumn = "id",
            entityColumn = "id"
    )
    public User user;
}

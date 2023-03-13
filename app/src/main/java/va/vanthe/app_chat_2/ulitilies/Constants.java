package va.vanthe.app_chat_2.ulitilies;

public class Constants {

    // version Room Database
    public static final int KEY_VERSION_ROOM = 2;
    //

    public static final String KEY_TYPE = "typeChat";
    public static final int KEY_TYPE_CHAT_SINGLE = 1;
    public static final int KEY_TYPE_CHAT_GROUP = 2;


    public static final String KEY_PREFERENCE_ACCOUNT = "account";

    public static final String KEY_USERS = "users";
    /// account
    public static final String KEY_ACCOUNT = "accounts";
    public static final String KEY_ACCOUNT_USER_ID = "userId";
    public static final String KEY_ACCOUNT_FIRST_NAME = "firstName";
    public static final String KEY_ACCOUNT_LAST_NAME = "lastName";
    public static final String KEY_ACCOUNT_SEX = "sex";
    public static final String KEY_ACCOUNT_DateOfBirth = "dateOfBrith";
    public static final String KEY_ACCOUNT_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_ACCOUNT_EMAIL = "email";
    public static final String KEY_ACCOUNT_PASSWORD = "password";
    public static final String KEY_ACCOUNT_IMAGE = "image";

    public static final String KEY_IS_SIGNED_IN = "isSignedIn";



    /// chat_message
    public static final String KEY_CHAT_MESSAGE = "chatMessage";
    public static final String KEY_CHAT_MESSAGE_ID = "chatMessageId";
    public static final String KEY_CHAT_MESSAGE_SENDER_ID = "senderId";
    public static final String KEY_CHAT_MESSAGE_MESSAGE = "message";
    public static final String KEY_CHAT_MESSAGE_DATA_TIME = "dataTime";
    public static final String KEY_CHAT_MESSAGE_CONVERSATION_ID = "conversationId";
    public static final String KEY_CHAT_MESSAGE_STYLE_MESSAGE = "styleMessage";
    public static final int KEY_CHAT_MESSAGE_STYLE_MESSAGE_NORMAL = 1;
    public static final int KEY_CHAT_MESSAGE_STYLE_MESSAGE_FILE = 2;
    public static final int KEY_CHAT_MESSAGE_STYLE_MESSAGE_LOCATION = 3;

    /// group_member: id, userId, conversationId, status
        public static final String KEY_GROUP_MEMBER = "groupMember";
    public static final String KEY_GROUP_MEMBER_ID = "groupMemberId";
    public static final String KEY_GROUP_MEMBER_USER_ID = "userId";
    public static final String KEY_GROUP_MEMBER_RECEIVER_ID = "receiverId";
    public static final String KEY_GROUP_MEMBER_CONVERSATION_ID = "conversationId";
    public static final String KEY_GROUP_MEMBER_STATUS = "status";

    /// conversation: id,
    public static final String KEY_CONVERSATION = "conversation";
    public static final String KEY_CONVERSATION_ID = "conversationId";
    public static final String KEY_CONVERSATION_CREATE_TIME = "createTime";
    public static final String KEY_CONVERSATION_SENDER_ID = "senderId";
    public static final String KEY_CONVERSATION_NEW_MESSAGE = "newMessage";
    public static final String KEY_CONVERSATION_MESSAGE_TIME = "messageTime";




    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";

    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";

    public static final String KEY_COLLECTION_CONVERSATION = "conversation";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_BACKGROUND = "background";
    public static final String KEY_SENDER_NICKNAME = "senderNickname";
    public static final String KEY_RECEIVER_NICKNAME = "receiverNickname";

    public static final String KEY_FCM_TOKEN = "fcmToken";

    public static final String KEY_COLLECTION_FRIEND = "friend";
    public static final String KEY_FRIEND_ID = "friendId";
    public static final String KEY_INVITATION_STATUS = "status";

    public static final String KEY_COLLECTION_GROUP_CHAT = "groupChat";
    public static final String KEY_ID_GROUP = "idGroup";
    public static final String KEY_NAME_GROUP = "nameGroup";
    public static final String KEY_IMAGE_GROUP = "imageGroup";
    public static final String KEY_NUMBER_PEOPLE = "numberPeople";
    public static final String KEY_PEOPLE_ID = "peopleId";
    public static final String KEY_PEOPLE_NAME = "peopleName";
    public static final String KEY_PEOPLE_IMAGE = "peopleImage";
    public static final String KEY_PEOPLE_NICKNAME = "peopleNickname";

    // trạng thái của cuộc trò chuyện
    // chat và group
    public static final String KEY_COLLECTION_STATUS = "collectionStatus";

    public static final String IMAGE_GROUP_CHAT_DEFAULT = "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";
    public static final String IMAGE_AVATAR_DEFAULT = "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";

    public static final String KEY_STATUS_LANGUAGE = "statusLanguage";
}

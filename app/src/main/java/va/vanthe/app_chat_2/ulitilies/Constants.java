package va.vanthe.app_chat_2.ulitilies;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Constants {

    // version Room Database
    public static final int KEY_VERSION_ROOM = 6;
    //

    public static final String KEY_TYPE = "typeChat";
    public static final int KEY_TYPE_CHAT_SINGLE = 1;
    public static final int KEY_TYPE_CHAT_GROUP = 2;

    public static final String KEY_IS_NEW_CHAT = "statusNewChat";




    public static final String KEY_USER = "user";

    /// account
    public static final String KEY_PREFERENCE_ACCOUNT = "account";
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
    public static final int KEY_CHAT_MESSAGE_STYLE_MESSAGE_TEXT = 1;
    public static final int KEY_CHAT_MESSAGE_STYLE_MESSAGE_IMAGE = 2;
    public static final int KEY_CHAT_MESSAGE_STYLE_MESSAGE_FILE = 3;
    public static final int KEY_CHAT_MESSAGE_STYLE_MESSAGE_LOCATION = 4;
    public static final int KEY_CHAT_MESSAGE_STYLE_MESSAGE_NOTIFICATION  = 99;

    /// group_member: id, userId, conversationId, status
        public static final String KEY_GROUP_MEMBER = "groupMember";
    public static final String KEY_GROUP_MEMBER_ID = "groupMemberId";
    public static final String KEY_GROUP_MEMBER_USER_ID = "userId";
    public static final String KEY_GROUP_MEMBER_TIME_JOIN = "timeJoin";
    public static final String KEY_GROUP_MEMBER_CONVERSATION_ID = "conversationId";
    public static final String KEY_GROUP_MEMBER_STATUS = "status";

    /// conversation: id,
    public static final String KEY_CONVERSATION = "conversation";
    public static final String KEY_CONVERSATION_ID = "conversationId";
    public static final String KEY_CONVERSATION_CREATE_TIME = "createTime";
    public static final String KEY_CONVERSATION_SENDER_ID = "senderId";
    public static final String KEY_CONVERSATION_NEW_MESSAGE = "newMessage";
    public static final String KEY_CONVERSATION_MESSAGE_TIME = "messageTime";
    public static final String KEY_CONVERSATION_STYLE_CHAT = "styleChat";
    public static final String KEY_CONVERSATION_BACKGROUND_IMAGE = "backgroundImage";

    public static final String KEY_IMAGE = "image";

    // Friend
    public static final String KEY_FRIEND = "friend";
    public static final String KEY_FRIEND_ID = "friendId";
    public static final String KEY_FRIEND_USER_ID = "userId";
    public static final String KEY_FRIEND_USER_FRIEND_ID = "userFriendId";
    public static final String KEY_FRIEND_STATUS = "status";
    public static final int KEY_FRIEND_STATUS_DAKETBAN = 2;
    public static final int KEY_FRIEND_STATUS_DAGUILOIMOI = 1;
    public static final int KEY_FRIEND_STATUS_NHANLOIMOI = 0;
    public static final String KEY_FRIEND_TIME_STAMP = "timeStamp";

    public static final String KEY_FCM_TOKEN = "fcmToken";

    public static final String KEY_STATUS_LANGUAGE = "statusLanguage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAA_R2sseg:APA91bErMsnv4ihNLi80tz-3FfyRc9SlZ7FTRC9gJVLfRaUM4pqjyfeA4cPEhJdL6MlWSMlzu19Yu11VwtYsdISeuBQX3zUCu5BZZBGQYyFMTXQPEIqKfvc2RuyXfg7C5K-m3QhmQdN3 "
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }

    public static final int KEY_REQUEST_CODE_BACKGROUND = 1;
    public static final int KEY_REQUEST_CODE_AVATAR = 2;
    public static final int KEY_REQUEST_CODE_IMAGE_MESSAGE = 3;

    public static final String EMOJI = "😀😃😄😁😆😅😂🤣☺️😊😇🙂🙃😉😌😍😘😗😙😚😋😛😝😜🤪🤨🧐🤓😎🥸🤩😏😒😞😔😟😕🙁☹️😣😖😫😩🥺😢😭😤😠😡🤬🤯😳🥵🥶😶‍🌫️😱😨😰😥😓🤗🤔🤭🤫🤥😶😐😑😬🙄😯😦😧😮😲🥱😴🤤😪😮‍💨😵😵‍💫🤐🥴🤢🤮🤧😷🤒🤕🤑🤠😈👿👹👺🤡💩👻💀☠️👽👾🤖🎃😺😸😹😻😼😽🙀😿😾🤲🙌👐👏🏻🤝👍👎👊✊🤛🤜🤞✌️🤟🤘👌🤌🤏";
    public static final List<String> EMOJI_LIST = Arrays.asList("😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "☺️", "😊", "😇", "🙂", "🙃","😉","😌","😍","😘","😗","😙","😚","😋","😛","😝","😜","🤪","🤨","🧐","🤓","😎","🤩","😏","😒","😞","😔","😟","😕","🙁","😣","😖","😫","😩","😢","😭","😤","😠","😡","🤬","🤯","😳","😶","😱","😨","😰","😥","😓","🤗","🤔","🤭","🤫","🤥","😶","😐","😑","😬","🙄","😯","😦","😧","😮","😲","😴","🤤","😪","😮","😵","😵","💫","🤐","🤢","🤮","🤧","😷","🤒","🤕","🤑","🤠","😈","👿","👹","👺","🤡","💩","👻","💀","☠","👽","👾","🤖","🎃","😺","😸","😹","😻","😼","😽","🙀","😿","😾","🤲","🙌","👐","👏","🤝","👍","👎","👊","✊","🤛","🤜","🤞","✌","🤟","🤘","👌");



}


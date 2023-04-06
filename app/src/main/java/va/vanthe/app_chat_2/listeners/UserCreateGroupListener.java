package va.vanthe.app_chat_2.listeners;


import va.vanthe.app_chat_2.entity.User;

public interface UserCreateGroupListener {
    void onUserClicked(User user, boolean checker);
}

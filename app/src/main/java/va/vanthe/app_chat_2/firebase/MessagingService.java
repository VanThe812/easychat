package va.vanthe.app_chat_2.firebase;


import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.C;

import java.util.Date;

import va.vanthe.app_chat_2.MyApplication;
import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.activities.ChatMessageActivity;
import va.vanthe.app_chat_2.activities.test;
import va.vanthe.app_chat_2.database.ConversationDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
//        Log.d("FCM", "Token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("FCM", remoteMessage.getData().toString());
        // lấy dữ liệu về
        String userId = remoteMessage.getData().get(Constants.KEY_ACCOUNT_USER_ID);
        String conversationId = remoteMessage.getData().get(Constants.KEY_CONVERSATION_ID);
        String message = remoteMessage.getData().get(Constants.KEY_CHAT_MESSAGE_MESSAGE);
        int styleMessage = Integer.parseInt(remoteMessage.getData().get(Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE));

        // gọi đến dữ liệu trong máy để lấy đầy đủ thông tin người dùng và cuộc hội thoại
        User user = UserDatabase.getInstance(this).userDAO().getUser(userId);
        Conversation conversation = ConversationDatabase.getInstance(this).conversationDAO().getOneConversation(conversationId);

        // tạo ra intent để khi ấn vào thông báo sẽ chuyển qua trang chat tương ứng
        Intent resultIntent = new Intent(this, ChatMessageActivity.class);
        resultIntent.putExtra(Constants.KEY_CONVERSATION, conversation);
        resultIntent.putExtra(Constants.KEY_TYPE, conversation.getStyleChat());
        resultIntent.putExtra(Constants.KEY_USER, user);
        //
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(getNotificationId(),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // gọi đến layout thông báo và config dữ liệu trong thông báo
        RemoteViews notificationLayout = new RemoteViews(this.getPackageName(), R.layout.layout_custom_natification);
        // config title and avatar notification
        if (conversation.getStyleChat() == Constants.KEY_TYPE_CHAT_SINGLE) {
            notificationLayout.setTextViewText(R.id.textViewTitleCustomNotification, user.getLastName());
            notificationLayout.setImageViewBitmap(R.id.imageAvatar, HelperFunction.getBitmapFromEncodedImageString(user.getImage()));
        } else if (conversation.getStyleChat() == Constants.KEY_TYPE_CHAT_GROUP) {
            notificationLayout.setTextViewText(R.id.textViewNameSender, user.getLastName() + ": ");
            notificationLayout.setTextViewText(R.id.textViewTitleCustomNotification, conversation.getConversationName());
            if (conversation.getConversationAvatar() != null)
                notificationLayout.setImageViewBitmap(R.id.imageAvatar, HelperFunction.getBitmapFromEncodedImageString(conversation.getConversationAvatar()));
        }
        // config messaging notification
        if (styleMessage == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_TEXT) {
            notificationLayout.setTextViewText(R.id.textViewMessageCustomNotification, message);
        } else if (styleMessage == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_IMAGE) {
            notificationLayout.setTextViewText(R.id.textViewMessageCustomNotification, "Đã gửi 1 ảnh");
            if (conversation.getConversationAvatar() != null) {
                notificationLayout.setImageViewBitmap(R.id.imageAvatar, HelperFunction.getBitmapFromEncodedImageString(conversation.getConversationAvatar()));
            }
            Log.e("getBytes", message);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("images")
                    .child("conversation")
                    .child(conversation.getId())
                    .child(message);
            storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Chuyển đổi mảng byte thành Bitmap
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Log.e("getBytes", bytes.toString());
                    // Sử dụng bitmap
                    notificationLayout.setImageViewBitmap(R.id.imageMessageImage, bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Xử lý khi có lỗi xảy ra
                    Log.e(MessagingService.class.toString(), "Error getting image from Firebase Storage.", e);
                }
            });
        }


        // Gửi thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_camera)
//                .setSound(sound)
                .setCustomContentView(notificationLayout)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(getNotificationId(), builder.build());
    }
    private void sendCustomNotification(Conversation conversation) {
        // Cài âm thanh thông báo
//        Uri sound = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.messaging);

        // Create an Intent for the activity you want to start


    }
    private int getNotificationId() {
        return (int) new Date().getTime();
    }
}

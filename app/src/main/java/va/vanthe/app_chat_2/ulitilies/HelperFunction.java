package va.vanthe.app_chat_2.ulitilies;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import va.vanthe.app_chat_2.MyApplication;
import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.activities.test;
import va.vanthe.app_chat_2.dataencrypt.SHA256Encryptor;
import va.vanthe.app_chat_2.entity.User;

public class HelperFunction {


    public static boolean isEmoji(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isHighSurrogate(str.charAt(i))) {
                int type = Character.getType(str.charAt(i));
                if (type != Character.SURROGATE && type != Character.OTHER_SYMBOL) {
                    return false;
                }
            } else {
                if (i < str.length() - 1 && Character.isLowSurrogate(str.charAt(i + 1))) {
                    int codepoint = Character.toCodePoint(str.charAt(i), str.charAt(i + 1));
                    if (codepoint >= 0x1F000 && codepoint <= 0x1F9FF) {
                        continue;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }
    public static void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
    public static String convertPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.length() > 1 && phoneNumber.charAt(0) == '0') {
            return "+84" + phoneNumber.substring(1);
        } else {
            return phoneNumber;
        }
    }
    public static boolean checkPassword(String password) {
        // Kiểm tra độ dài mật khẩu
        if (password.length() < 8) {
            return false;
        }

        // Kiểm tra có chữ hoa, chữ thường, chữ số và ký tự đặc biệt hay không
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecialChar = true;
            }
        }

        // Nếu đủ các yêu cầu thì trả về true, ngược lại trả về false
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }


//    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//    //Trả về 1 cursor - quản lý dữ liệu contact trên điện thoại
//    Cursor cursor  = getContext().getContentResolver().query(uri, null, null, null, null);
//    int i = 1;
//        while (cursor.moveToNext()) {
//        String tenCotPhone = ContactsContract.CommonDataKinds.Phone.NUMBER;
//
//        int viTriCotPhone = cursor.getColumnIndex(tenCotPhone);
//
//        String phone = cursor.getString(viTriCotPhone);
//        User user = new User();
//        user.setFirstName("Người dùng");
//        user.setLastName("Easy Chat" + i);
//        user.setPassword(SHA256Encryptor.encrypt("1"));
//        user.setSex(true);
//        user.setDateOfBrith("08/10/2002");
//        user.setPhoneNumber(HelperFunction.convertPhoneNumber(phone.replaceAll("[^0-9]", "")));
//        i++;
//
//        database.collection(Constants.KEY_USER)
//                .add(user.toHashMap())
//                .addOnFailureListener(runnable -> {
//
//                });
//    }


//    public static void sendCustomNotification(AppCompatActivity appCompatActivity, Uri sound, Bitmap bitmapImage, ) {
//        // Cài âm thanh thông báo
////        Uri sound = Uri.parse("android.resource://" + appCompatActivity.getActivity().getPackageName() + "/" + R.raw.messaging);
//
//        // Create an Intent for the activity you want to start
//        Intent resultIntent = new Intent(appCompatActivity.getApplicationContext(), test.class);
//        resultIntent.putExtra("hii", "xin chao");
//        // Create the TaskStackBuilder and add the intent, which inflates the back stack
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
//        stackBuilder.addNextIntentWithParentStack(resultIntent);
//        // Get the PendingIntent containing the entire back stack
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(getNotificationId(),
//                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//
//        RemoteViews notificationLayout = new RemoteViews(getActivity().getPackageName(), R.layout.layout_custom_natification);
//        notificationLayout.setTextViewText(R.id.textViewTitleCustomNotification, "Title custom notification");
//        notificationLayout.setTextViewText(R.id.textViewMessageCustomNotification, "Văn Thế đã gửi một hình ảnh");
//        notificationLayout.setImageViewBitmap(R.id.imageMessageImage, bitmapImage);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), MyApplication.CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_camera)
//                .setSound(sound)
//                .setCustomContentView(notificationLayout)
//                .setContentIntent(resultPendingIntent)
//                .setAutoCancel(true);
//
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
//        notificationManager.notify(getNotificationId(), builder.build());
//
//    }
}

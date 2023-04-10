package va.vanthe.app_chat_2.ulitilies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class HelperFunction {
    public static Bitmap getBitmapFromEncodedImageString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    public static String getEncodedImageStringFromBitmap(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
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

}

package va.vanthe.app_chat_2.ulitilies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class HelperFunction {
    public static Bitmap getBitmapFromEncodedImageString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}

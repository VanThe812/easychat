package va.vanthe.app_chat_2.ulitilies;

import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageTypeDetector {
    public static String detect(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        String mimeType = Files.probeContentType(path);
        if (mimeType == null) {
            mimeType = URLConnection.guessContentTypeFromName(path.getFileName().toString());
        }
        return mimeType;
    }
}

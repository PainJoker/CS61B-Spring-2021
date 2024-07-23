package gitlet;

import java.io.File;
import java.io.IOException;

/**
 * Handle the file or directory creation.
 *
 * @author PainJoker
 */
public class FileUtils {
    public static void makeDir(File dir) {
        if (!dir.mkdir()) {
            throw new RuntimeException("Failed to create directory " + dir);
        }
    }

    public static void createFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

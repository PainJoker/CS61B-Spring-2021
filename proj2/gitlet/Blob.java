package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a file tracker.
 *
 *  @author PainJoker
 */
public class Blob implements Serializable {
    private final String fileName;
    private final String content;
    private final String uid;

    public Blob(String fileName) {
        this.fileName = fileName;
        File filePath = join(CWD, fileName);
        content = readContentsAsString(filePath);
        uid = sha1(fileName, content);
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }

    public String getUid() {
        return uid;
    }
}

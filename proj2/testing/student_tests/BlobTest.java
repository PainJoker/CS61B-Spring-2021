package testing.student_tests;

import gitlet.Blob;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.writeContents;
import static org.junit.Assert.*;

public class BlobTest {
    @Test
    public void ContentTest() throws IOException {
        File testFile = new File("./test1.txt");
        testFile.createNewFile();
        String testContent = "This is a test";
        writeContents(testFile, testContent);
        Blob testBlob = new Blob(testFile.getName());
        assertEquals(testContent, testBlob.getContent());
        assertEquals(testFile.getName(), testBlob.getFileName());
    }

}

package testing.student_tests;

import gitlet.Commit;
import org.junit.Test;
import static org.junit.Assert.*;

public class CommitTest {
    @Test
    public void testInitialCommit() {
        Commit initialCommit = new Commit();
        String unixEpoch = "Thu Jan 01 08:00:00 CST 1970";
        assertEquals(unixEpoch, initialCommit.getDate());
        assertEquals(40, initialCommit.getUid().length());
        assertEquals("initial commit", initialCommit.getMessage());
        assertEquals(0, initialCommit.getParentUid().length());
        assertEquals(0, initialCommit.getFiles().size());
    }
}

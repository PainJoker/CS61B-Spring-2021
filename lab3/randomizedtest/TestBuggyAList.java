package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        BuggyAList<Integer> buggy = new BuggyAList<>();
        AListNoResizing<Integer> noResizing = new AListNoResizing<>();

        for (int i = 0; i < 3; i++) {
            buggy.addLast(i);
            noResizing.addLast(i);
        }
        for (int i = 0; i < 3; i++) {
            int buggyRes = buggy.removeLast();
            int noResizingRes = noResizing.removeLast();
            assertEquals(buggyRes, noResizingRes);
        }
    }

    @Test
    public void RandomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggy.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int LSize = L.size();
                int BSize = buggy.size();
                assertEquals(LSize, BSize);
            } else if (operationNumber == 2) {
                if (L.size() <= 0 || buggy.size() <= 0) {
                    continue;
                }
                int lastL = L.removeLast();
                int lastB = buggy.removeLast();
                assertEquals(lastL, lastB);
            } else if (operationNumber == 3) {
                if (L.size() <= 0 || buggy.size() <= 0) {
                    continue;
                }
                int lastL = L.getLast();
                int lastB = buggy.getLast();
                assertEquals(lastL, lastB);
            }
        }
    }
}

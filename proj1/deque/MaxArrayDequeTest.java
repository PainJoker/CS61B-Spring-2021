package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    public static class AlwaysItem1 implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            return 1;
        }
    }

    public static class IntGreater implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    }

    @Test
    public void alwaysItem1Test() {
        AlwaysItem1 alwaysItem1 = new AlwaysItem1();
        MaxArrayDeque<Integer> testArray = new MaxArrayDeque<>(alwaysItem1);

        for (int i = 0; i < 50; i++) {
            testArray.addLast(i);
        }
        assertEquals(0, (int) testArray.max());
    }

    @Test
    public void intGreaterTest() {
        IntGreater intGreater = new IntGreater();
        MaxArrayDeque<Integer> testArray = new MaxArrayDeque<>(intGreater);

        for (int i = 0; i < 50; i++) {
            testArray.addLast(i);
        }
        assertEquals(49, (int) testArray.max());
    }
}

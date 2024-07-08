package deque;

import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    public static class AlwaysGreater implements Comparator {
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

    public static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String d1, String d2) {
            return d1.compareTo(d2);
        }
    }

    @Test
    public void alwaysItem1Test() {
        AlwaysGreater alwaysGreater = new AlwaysGreater();
        MaxArrayDeque<Integer> testArray = new MaxArrayDeque<>(alwaysGreater);

        for (int i = 0; i < 50; i++) {
            testArray.addLast(i);
        }
        assertEquals(49, (int) testArray.max());
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

    @Test
    public void stringComparatorTest() {
        StringComparator sc = new StringComparator();
        MaxArrayDeque<String> testArray = new MaxArrayDeque<>(sc);
        testArray.addLast("a");
        testArray.addLast("b");
        testArray.addLast("c");

        assertEquals("c", testArray.max());
    }
}

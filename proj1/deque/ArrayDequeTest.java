package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<>();
        assertTrue(ad1.isEmpty());

        ad1.addFirst("first");
        assertEquals(1, ad1.size());
        assertFalse("lld1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }

    @Test
    public void addRemoveTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        // should be empty
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create ArrayDeque with different parameterized types*/
    public void multipleParamTest() {

        ArrayDeque<String> ad1 = new ArrayDeque<String>();
        ArrayDeque<Double> ad2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<Boolean>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ad1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ad1.removeLast());

    }

    @Test
    public void bigLLDequeTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }
    }

    @Test
    public void randomTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        int testNum = 100000;
        for (int i = 0; i < testNum; i++) {
            int operationIdx = StdRandom.uniform(0, 4);
            if (operationIdx == 0) {
                deque.addFirst(i);
            } else if (operationIdx == 1) {
                deque.addLast(i);
            } else if (operationIdx == 2) {
                deque.removeFirst();
            } else if (operationIdx == 3) {
                deque.removeLast();
            }
        }
    }

    @Test
    public void iteratorTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < 10; i++) {
            deque.addLast(i);
        }

        int idx = 0;
        for (int i : deque) {
            assertEquals(i, idx);
            idx++;
        }

        while (!deque.isEmpty()) {
            deque.removeFirst();
        }

        for (int i : deque) {
            assertNull("Should return null", i);
        }
    }

    @Test
    public void equalsTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < 10; i++) {
            deque.addLast(i);
        }

        assertNotEquals(null, deque);
        assertNotEquals("String Test", deque);

        ArrayDeque<Integer> deque2 = new ArrayDeque<>();
        for (int i = 0; i < 10; i++) {
            deque2.addLast(i);
        }
        assertTrue(deque.equals(deque2));
    }
}

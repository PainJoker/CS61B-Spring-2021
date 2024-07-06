package flik;

import org.junit.Test;
import static org.junit.Assert.*;

public class FlikTest {
    @Test
    public void largeTest() {
        for (int i = 0, j = 0; i < 100000; ++i, ++j) {
            assertTrue("Should be same.", Flik.isSameNumber(i, j));
        }
    }
}

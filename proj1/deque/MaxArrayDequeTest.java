package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    @Test
    public void max() {
        MaxArrayDeque<Integer> maxArrayDeque = new MaxArrayDeque<>(Comparator.<Integer>naturalOrder());
        for (int i = 0; i < 100; i++) {
            maxArrayDeque.addFirst(i);
        }
        assertEquals(99, maxArrayDeque.max().intValue());
    }

    @Test
    public void testMax() {
        MaxArrayDeque<Integer> maxArrayDeque = new MaxArrayDeque<>(Comparator.<Integer>reverseOrder());
        for (int i = 0; i < 100; i++) {
            maxArrayDeque.addFirst(i);
        }
        assertEquals(0, maxArrayDeque.max().intValue());
    }
}
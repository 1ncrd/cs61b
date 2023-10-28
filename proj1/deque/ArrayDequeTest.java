package deque;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void basicTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addLast(1);
        arrayDeque.addLast(2);
        arrayDeque.addLast(3);
        assertEquals("Size should be 3", 3, arrayDeque.size());
        arrayDeque.printDeque();
    }

    @Test
    public void resizeTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 30; i++) {
            arrayDeque.addLast(i);
        }
        assertEquals("Size should be 30", 30, arrayDeque.size());
        arrayDeque.printDeque();
    }

    @Test
    public void addFirstLastTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 30; i++) {
            arrayDeque.addFirst(i);
        }
        for (int i = 0; i < 30; i++) {
            arrayDeque.addLast(i);
        }
        assertEquals("Size should be 60", 60, arrayDeque.size());
        for (int i = 0; i < 30; i++) {
            assertEquals(29 - i, arrayDeque.get(i).intValue());
        }
        for (int i = 0; i < 30; i++) {
            assertEquals(i, arrayDeque.get(i + 30).intValue());
        }
        arrayDeque.printDeque();
    }

    @Test
    public void addFirstLastTest2() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst(1);
        arrayDeque.addLast(2);
        arrayDeque.addFirst(3);
        arrayDeque.addLast(4);
        int[] expected = new int[]{3, 1, 2, 4};
        for (int i = 0; i < 4; i++) {
            assertEquals(expected[i], arrayDeque.get(i).intValue());
        }
    }

    @Test
    public void removeTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst(1);
        arrayDeque.addLast(2);
        arrayDeque.addFirst(3);
        arrayDeque.addLast(4);
        arrayDeque.removeFirst();
        arrayDeque.removeLast();
        int[] expected = new int[]{1, 2};
        for (int i = 0; i < 2; i++) {
            assertEquals(expected[i], arrayDeque.get(i).intValue());
        }
    }
}

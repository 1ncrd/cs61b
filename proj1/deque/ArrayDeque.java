package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] array;
    private int size;
    private int capacity;
    private int first;
    private int last;
    public static final int INITIAL_SIZE = 8;

    public ArrayDeque() {
        this(INITIAL_SIZE);
    }
    public ArrayDeque(int capacity) {
        this.size = 0;
        this.capacity = capacity;
        this.first = 0;
        this.last = 0;
        this.array = (T[]) new Object[capacity];
    }

    private void increase(int newCapacity) {
        if (newCapacity <= capacity)  {
            throw new IllegalArgumentException("New capacity should be greater than old capacity");
        }
        T[] newArray = (T[]) new Object[newCapacity];
        if (last > first) {
            System.arraycopy(array, first, newArray, 0, size);
        } else {
            System.arraycopy(array, first, newArray, 0, capacity - first);
            System.arraycopy(array, 0, newArray, capacity - first, last);
        }
        this.first = 0;
        this.last = size;
        this.array = newArray;
        this.capacity = newCapacity;
    }

    private static int dec(int i, int j, int mod) {
        return Math.floorMod(i - j, mod);
    }

    private static int inc(int i, int j, int mod) {
        return Math.floorMod(i + j, mod);
    }

    @Override
    public void addFirst(T item) {
        if (size == capacity) {
            increase(2 * capacity);
        }
        first = dec(first, 1, capacity);
        array[first] = item;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == capacity) {
            increase(2 * capacity);
        }
        array[last] = item;
        last = inc(last, 1, capacity);
        size++;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (T value : this) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size <= 0) {
            return null;
        }
        T value = array[first];
        array[first] = null;
        first = inc(first, 1, capacity);
        size--;
        return value;
    }

    @Override
    public T removeLast() {
        if (size <= 0) {
            return null;
        }
        last = dec(last, 1, capacity);
        T value = array[last];
        array[last] = null;
        size--;
        return value;
    }

    @Override
    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        return array[inc(first, index, capacity)];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator<T>(this);
    }

    private static class ArrayDequeIterator<T> implements Iterator<T> {
        ArrayDeque<T> arrayDeque;
        int current;

        public ArrayDequeIterator(ArrayDeque<T> arrayDeque) {
            this.arrayDeque = arrayDeque;
            this.current = arrayDeque.first;
        }
        @Override
        public boolean hasNext() {
            return current != arrayDeque.last;
        }

        @Override
        public T next() {
            T value = arrayDeque.array[current];
            current = inc(current, 1, arrayDeque.capacity);
            return value;
        }
    }
}

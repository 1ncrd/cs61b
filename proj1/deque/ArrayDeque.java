package deque;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] array;
    private int size;
    private int capacity;
    private int first;
    private int last;
    private static final int INITIAL_SIZE = 8;
    private static final float USAGE_FACTOR = 0.25f;
    private static final int SHRINK_FACTOR = 2;

    public ArrayDeque() {
        this.size = 0;
        this.capacity = INITIAL_SIZE;
        this.first = 0;
        this.last = 0;
        this.array = (T[]) new Object[INITIAL_SIZE];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArrayDeque<?> that = (ArrayDeque<?>) o;
        if (this.size() != that.size()) {
            return false;
        }
        var iter1 = this.iterator();
        var iter2 = that.iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            var v1 = iter1.next();
            var v2 = iter2.next();
            if (!Objects.equals(v1, v2)) {
                return false;
            }
        }
        return true;
    }

    private void increase(int newCapacity) {
        if (newCapacity <= capacity)  {
            throw new IllegalArgumentException("New capacity should be greater than old capacity");
        }
        copyToNewArray(newCapacity);
    }

    private void decrease(int newCapacity) {
        if (newCapacity >= capacity)  {
            throw new IllegalArgumentException("New capacity should be smaller than old capacity");
        }
        copyToNewArray(newCapacity);
    }

    private void copyToNewArray(int newCapacity) {
        if (this.size > newCapacity) {
            throw new IllegalArgumentException("New capacity should be greater than size");
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
        if (capacity >= 16 && (float) size / capacity < USAGE_FACTOR) {
            decrease(capacity / SHRINK_FACTOR);
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
        if (capacity >= 16 && (float) size / capacity < USAGE_FACTOR) {
            decrease(capacity / SHRINK_FACTOR);
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

        ArrayDequeIterator(ArrayDeque<T> arrayDeque) {
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

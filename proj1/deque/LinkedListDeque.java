package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private static class ListNode<T> {
        private T value;
        private ListNode<T> prev;
        private ListNode<T> next;
        ListNode() {
            this(null, null, null);
        }

        ListNode(T value) {
            this(value, null, null);
        }

        ListNode(T value, ListNode<T> prev, ListNode<T> next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }
    private int size;
    private final ListNode<T> sentinel;
    public LinkedListDeque() {
        size = 0;
        sentinel = new ListNode<>(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedListDeque<?> that = (LinkedListDeque<?>) o;
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

    @Override
    public int hashCode() {
        return Objects.hash(size, sentinel);
    }

    @Override
    public void addFirst(T item) {
        size++;
        ListNode<T> newNode = new ListNode<>(item);
        newNode.next = sentinel.next;
        newNode.prev = sentinel;
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
    }

    @Override
    public void addLast(T item) {
        size++;
        ListNode<T> newNode = new ListNode<>(item);
        newNode.next = sentinel;
        newNode.prev = sentinel.prev;
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
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
        if (size > 0) {
            size--;
        }
        T value = sentinel.next.value;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        return value;
    }

    @Override
    public T removeLast() {
        if (size > 0) {
            size--;
        }
        T value = sentinel.prev.value;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        return value;
    }

    @Override
    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        ListNode<T> current = sentinel.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;

    }

    public T getRecursive(int index) {
        if (index > size - 1) {
            return null;
        }

        return getRecursive(index, sentinel.next);
    }

    private T getRecursive(int index, ListNode<T> listNode) {
        if (index == 0) {
            return listNode.value;
        }
        return getRecursive(index - 1, listNode.next);
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator<>(this);
    }

    private static class LinkedListDequeIterator<T> implements Iterator<T> {
        LinkedListDeque<T> linkedListDeque;
        ListNode<T> current;

        LinkedListDequeIterator(LinkedListDeque<T> linkedListDeque) {
            this.linkedListDeque = linkedListDeque;
            this.current = linkedListDeque.sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return this.current != this.linkedListDeque.sentinel;
        }

        @Override
        public T next() {
            T value = current.value;
            current = current.next;
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

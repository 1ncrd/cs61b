package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public MaxArrayDeque(int capacity, Comparator<T> comparator) {
        super(capacity);
        this.comparator = comparator;
    }

    public T max() {
        return max(this.comparator);
    }

    public T max(Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }
        T maxElement = this.get(0);
        for (var e: this) {
            if (c.compare(e, maxElement) > 0) {
                maxElement = e;
            }
        }
        return maxElement;
    }
}

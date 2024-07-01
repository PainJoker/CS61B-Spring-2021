package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> comparator) {
        super();
        this.comparator = comparator;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T target = get(0);
        for (int i = 1; i < size(); i++) {
            T comparedItem = get(i);
            if (comparator.compare(target, comparedItem) < 0) {
                target = comparedItem;
            }
        }
        return target;
    }

    public T max() {
        return max(comparator);
    }
}


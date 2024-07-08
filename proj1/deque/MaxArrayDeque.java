package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        }
        T maxItem = get(0);
        for (int i = 0; i < this.size(); i++) {
            T iterItem = get(i);
            if (c.compare(iterItem, maxItem) > 0) {
                maxItem = iterItem;
            }
        }
        return maxItem;
    }

    public T max() {
        return max(comparator);
    }
}

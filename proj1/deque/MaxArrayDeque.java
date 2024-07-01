package deque;

import java.util.Comparator;

public class MaxArrayDeque<itemType> extends ArrayDeque<itemType> {
    private Comparator<itemType> comparator;

    public MaxArrayDeque(Comparator<itemType> comparator) {
        super();
        this.comparator = comparator;
    }

    public itemType max(Comparator<itemType> c) {
        if (isEmpty()) {
            return null;
        }
        itemType target = get(0);
        for (int i = 1; i < size(); i++) {
            itemType comparedItem = get(i);
            if (comparator.compare(target, comparedItem) < 0) {
                target = comparedItem;
            }
        }
        return target;
    }

    public itemType max() {
        return max(comparator);
    }
}


package deque;

public class ArrayDeque<itemType> {
    private itemType[] array;
    private int size;
    private int nextFront;
    private int nextRear;

    public ArrayDeque() {
        array = (itemType[]) new Object[8];
        nextFront = 7;
        nextRear = 0;
        size = 0;
    }

    public ArrayDeque(ArrayDeque<itemType> other) {
        array = (itemType[]) new Object[other.size() * 2];
        nextFront = other.size() * 2 - 1;
        nextRear = 0;
        for (int i = 0; i < other.size(); i++) {
            itemType item = other.get(i);
            addLast(item);
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void addFirst(itemType item) {
        if (isFull()) {
            resize(array.length * 2);
        }
        array[nextFront] = item;
        // source: https://stackoverflow.com/questions/4412179/best-way-to-make-javas-modulus-behave-like-it-should-with-negative-numbers
        nextFront = ((nextFront - 1) % array.length + array.length) % array.length;
        size += 1;
    }

    public void addLast(itemType item) {
        if (isFull()) {
            resize(array.length * 2);
        }
        array[nextRear] = item;
        nextRear = (nextRear + 1) % array.length;
        size += 1;
    }

    public itemType removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (array.length > 16 && size < array.length / 4) {
            resize(array.length / 2);
        }
        int resultIndex = (nextFront + 1) % array.length;
        itemType result = array[resultIndex];
        array[resultIndex] = null;
        size -= 1;
        return result;
    }

    public itemType removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (array.length > 16 && size < array.length / 4) {
            resize(array.length / 2);
        }
        int resultIndex = ((nextRear - 1) + array.length) % array.length;
        itemType result = array[resultIndex];
        array[resultIndex] = null;
        size -= 1;
        return result;
    }

    public itemType get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return array[(nextFront + 1) % array.length];
    }

    public void printDeque() {
        if (isEmpty()) {
            return;
        }
        for (int i = (nextFront + 1) % array.length; i != nextRear; i = (i + 1) % array.length) {
            System.out.print(array[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

    private int frontIndex() {
        return ((nextFront - 1) + array.length) % array.length;
    }

    private int rearIndex() {
        return (nextRear + 1) % array.length;
    }

    private boolean isFull() {
        return size == array.length;
    }

    private void resize(int capacity) {
        itemType[] newArray = (itemType[]) new Object[capacity];
        for (int i = (nextFront + 1) % array.length, j = 0; i != nextRear; i = (i + 1) % array.length, j++) {
            newArray[i] = array[j];
        }
        array = newArray;
        nextFront = capacity;
        nextRear = size;
    }
}

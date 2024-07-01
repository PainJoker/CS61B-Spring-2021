package deque;

public class ArrayDeque<itemType> implements Deque<itemType> {
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
        array = (itemType[]) new Object[other.size()];
        nextFront = other.size() - 1;
        nextRear = 0;
        for (int i = 0; i < other.size(); i++) {
            itemType item = other.get(i);
            addLast(item);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(itemType item) {
        if (isFull()) {
            resize(array.length * 2);
        }
        array[nextFront] = item;
        // source: https://stackoverflow.com/questions/4412179/best-way-to-make-javas-modulus-behave-like-it-should-with-negative-numbers
        moveToNextFront();
        size += 1;
    }

    @Override
    public void addLast(itemType item) {
        if (isFull()) {
            resize(array.length * 2);
        }
        array[nextRear] = item;
        moveToNextRear();
        size += 1;
    }

    @Override
    public itemType removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (array.length > 16 && size < array.length / 4) {
            resize(array.length / 2);
        }
        int resultIndex = frontIndex();
        itemType result = array[resultIndex];
        array[resultIndex] = null;
        size -= 1;
        nextFront = (nextFront + 1) % array.length;
        return result;
    }

    @Override
    public itemType removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (array.length > 16 && size < array.length / 4) {
            resize(array.length / 2);
        }
        int resultIndex = rearIndex();
        itemType result = array[resultIndex];
        array[resultIndex] = null;
        size -= 1;
        nextRear = ((nextRear - 1 + array.length) % array.length);
        return result;
    }

    @Override
    public itemType get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return array[(nextFront + index + 1) % array.length];
    }

    @Override
    public void printDeque() {
        if (isEmpty()) {
            return;
        }
        int i = frontIndex();
        for (int j = 0; j < size; j++) {
            System.out.print(get(i));
            System.out.print(" ");
            i = (i + 1) % array.length;
        }
        System.out.println();
    }

    private int frontIndex() {
        return (nextFront + 1) % array.length;
    }

    private int rearIndex() {
        return (nextRear -1 + array.length) % array.length;
    }

    private void moveToNextFront() {
        nextFront = (nextFront -1 + array.length) % array.length;
    }

    private void moveToNextRear() {
        nextRear = (nextRear + 1) % array.length;
    }

    private boolean isFull() {
        return size == array.length;
    }

    private void resize(int capacity) {
        itemType[] newArray = (itemType[]) new Object[capacity];
        for (int i = 0, j = frontIndex(); i < size; i++, j++) {
            newArray[i] = array[j];
        }
        array = newArray;
        nextFront = capacity - 1;
        nextRear = size;
    }
}

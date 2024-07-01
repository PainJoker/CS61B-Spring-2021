package deque;

public interface Deque<T> {
    /**
     * Add item to the first(index = 0) position.
     * @param item
     */
    void addFirst(T item);

    /**
     * Add item to the last position.
     * @param item
     */
    void addLast(T item);

    /**
     * Check the deque whether is empty.
     * @return true if the deque is empty
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * @return the size of the deque.
     */
    int size();

    /**
     * Print every element in deque separated by space.
     */
    void printDeque();

    /**
     * Delete the first element in deque.
     */
    T removeFirst();

    /**
     * Delete the last element in deque.
     */
    T removeLast();

    /**
     * Retrieve the element in corresponding position.
     * @param index start from 0.
     */
    T get(int index);
}

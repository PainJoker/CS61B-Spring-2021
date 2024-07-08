package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private Node<T> sentinel;
    private int size;
    public LinkedListDeque() {
        size = 0;
        sentinel = new Node<>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel.prev;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(T item) {
        Node<T> newItem = new Node<>(item, sentinel, sentinel.next);
        sentinel.next.prev = newItem;
        sentinel.next = newItem;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node<T> newItem = new Node<>(item, sentinel.prev, sentinel);
        sentinel.prev.next = newItem;
        sentinel.prev = newItem;
        size += 1;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node<T> firstNode = sentinel.next;
        T removedItem = firstNode.item;
        sentinel.next = firstNode.next;
        firstNode.next.prev = sentinel;
        size -= 1;
        return removedItem;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node<T> lastNode = sentinel.prev;
        T removedItem = lastNode.item;
        sentinel.prev = lastNode.prev;
        lastNode.prev.next = sentinel;
        size -= 1;
        return removedItem;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node<T> node = sentinel.next;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node.item;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, Node<T> node) {
        if (index == 0) {
            return node.item;
        } else {
            return getRecursiveHelper(index - 1, node.next);
        }
    }

    @Override
    public void printDeque() {
        Node<T> current = sentinel;
        while (current.next != sentinel) {
            System.out.print(current.next.item);
            System.out.print(" ");
            current = current.next;
        }
        System.out.println();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        if (size != ((Deque<?>) o).size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(((Deque<?>) o).get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new LLDIterator();
    }

    private static class Node<T> {
        private T item;
        private Node<T> prev;
        private Node<T> next;

        Node(T item, Node<T> prev, Node<T> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private class LLDIterator implements Iterator<T> {
        private Node<T> wizard;

        LLDIterator() {
            wizard = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return wizard != sentinel;
        }

        @Override
        public T next() {
            T val = wizard.item;
            wizard = wizard.next;
            return val;
        }
    }
}

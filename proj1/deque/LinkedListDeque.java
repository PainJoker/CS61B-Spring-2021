package deque;

public class LinkedListDeque<itemType> {
    private Node<itemType> sentinel;
    private int size;
    public LinkedListDeque() {
        size = 0;
        sentinel = new Node<itemType>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel.prev;
    }

    public LinkedListDeque(LinkedListDeque other) {
        size = 0;
        sentinel = new Node<itemType>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel.prev;
        Node temp = other.sentinel;
        while (temp.next != other.sentinel) {
            addLast((itemType) temp.next.item);
            temp = temp.next;
        }
    }

    public boolean isEmpty() {
        return size == 0 && sentinel.next == sentinel.prev;
    }

    public int size() {
        return size;
    }

    public void addFirst(itemType item) {
        Node<itemType> newItem = new Node<>(item, sentinel, sentinel.next);
        sentinel.next.prev = newItem;
        sentinel.next = newItem;
        size += 1;
    }

    public void addLast(itemType item) {
        Node<itemType> newItem = new Node<>(item, sentinel.prev, sentinel);
        sentinel.prev.next = newItem;
        sentinel.prev = newItem;
        size += 1;
    }

    public itemType removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node<itemType> firstNode = sentinel.next;
        itemType removedItem = firstNode.item;
        sentinel.next = firstNode.next;
        firstNode.next.prev = sentinel;
        size -= 1;
        return removedItem;
    }

    public itemType removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node<itemType> lastNode = sentinel.prev;
        itemType removedItem = lastNode.item;
        sentinel.prev = lastNode.prev;
        lastNode.prev.next = sentinel;
        size -= 1;
        return removedItem;
    }

    public itemType get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node<itemType> node = sentinel;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node.item;
    }

    public itemType getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }

    private itemType getRecursiveHelper(int index, Node<itemType> node) {
        if (index == 0) {
            return node.item;
        } else {
            return getRecursiveHelper(index - 1, node.next);
        }
    }

    public void printDeque() {
        Node<itemType> current = sentinel;
        while (current.next != sentinel) {
            System.out.print(current.next.item);
            System.out.print(" ");
            current = current.next;
        }
        System.out.println();
    }

    private static class Node<nodeType> {
        private nodeType item;
        private Node<nodeType> prev;
        private Node<nodeType> next;

        Node(nodeType item, Node<nodeType> prev, Node<nodeType> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

}

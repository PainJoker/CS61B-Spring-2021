package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author PainJoker
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int capacity;
    private final double loadFactor;
    private HashSet<K> keys;

    /** Constructors */
    public MyHashMap() {
        capacity = 16;
        loadFactor = 0.75;
        buckets = createTable(capacity);
        keys = new HashSet<>();
    }

    public MyHashMap(int initialSize) {
        capacity = initialSize;
        loadFactor = 0.75;
        buckets = createTable(capacity);
        keys = new HashSet<>();
    }

    /*
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        capacity = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(capacity);
        keys = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    @Override
    public void clear() {
        for (Collection<Node> bucket : buckets) {
            if (bucket != null) {
                bucket.clear();
                bucket = null;
            }
        }
        keys.clear();
    }

    @Override
    public boolean containsKey(K key) {
        return keys.contains(key);
    }

    @Override
    public V get(K key) {
        if (!keys.contains(key)) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), capacity);
        Collection<Node> bucket = buckets[index];
        V result = null;
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                result = node.value;
            }
        }
        return result;
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public void put(K key, V value) {
        assert key != null;
        if (keys.contains(key)) {
            update(key, value);
            return;
        }
        if (needArgument()) {
            argument();
        }
        int index = Math.floorMod(key.hashCode(), capacity);
        if (buckets[index] == null) {
            buckets[index] = createBucket();
        }
        buckets[index].add(createNode(key, value));
        keys.add(key);
    }

    private void update(K key, V value) {
        int index = Math.floorMod(key.hashCode(), capacity);
        Collection<Node> bucket = buckets[index];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                node.value = value;
            }
        }
    }

    private boolean needArgument() {
        return (keys.size() + 1)  >= loadFactor * capacity;
    }

    private void argument() {
        int targetCapacity = capacity * 2;
        Collection[] temp = createTable(targetCapacity);
        for (K key : keys) {
            int index = Math.floorMod(key.hashCode(), targetCapacity);
            V value = get(key);
            if (temp[index] == null) {
                temp[index] = createBucket();
            }
            temp[index].add(createNode(key, value));
        }
        buckets = temp;
        capacity = targetCapacity;
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public Iterator<K> iterator() {
        return keys.iterator();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("Not supported in Lab8.");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("Not supported in Lab8.");
    }

}

package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private MapNode<K, V> map;
    private BSTMap<K, V> left;
    private BSTMap<K, V> right;

    public BSTMap() {
        map = null;
        left = null;
        right = null;
    }

    @Override
    public void clear() {
        clearRecursive(this);
        this.map = null;
    }

    private static void clearRecursive(BSTMap tree) {
        if (tree == null) {
            return;
        }
        clearRecursive(tree.left);
        tree.left = null;
        clearRecursive(tree.right);
        tree.right = null;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKeyRecursive(this, key);
    }

    @Override
    public V get(K key) {
        return getRecursive(this, key);
    }

    private V getRecursive(BSTMap<K, V> tree, K key) {
        if (tree == null || tree.map == null) {
            return null;
        }
        int cmp = key.compareTo(tree.map.getKey());
        if (cmp == 0) {
            return tree.map.getValue();
        } else if (cmp < 0) {
            return getRecursive(tree.left, key);
        } else {
            return getRecursive(tree.right, key);
        }
    }

    @Override
    public int size() {
        return sizeRecursive(this);
    }

    @Override
    public void put(K key, V value) {
        putRecursive(this, key, value);
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("Not supported in lab7.");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("Not supported in lab7.");
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("Not supported in lab7.");
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("Not supported in lab7.");
    }

    public void printInOrder() {
        /* Actual debug function, printInOrder is not implemented.*/
        print();
    }

    private void print() {
        printRecursive(this, 0);
    }

    private void printRecursive(BSTMap<K, V> tree, int indent) {
        if (tree == null || tree.map == null) {
            return;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
        System.out.println(tree.map.getKey().toString());
        printRecursive(tree.left, indent + 1);
        printRecursive(tree.right, indent + 1);
    }

    private BSTMap<K, V> putRecursive(BSTMap<K, V> tree, K key, V value) {
        if (tree == null) {
            tree = new BSTMap<>();
            tree.map = new MapNode<>(key, value);
            return tree;
        }
        if (tree.map == null) {
            tree.map = new MapNode<>(key, value);
            return this;
        }
        int cmp = key.compareTo(tree.map.getKey());
        if (cmp == 0) {
            return this;
        } else if (cmp < 0) {
            tree.left = putRecursive(tree.left, key, value);
        } else {
            tree.right = putRecursive(tree.right, key, value);
        }
        return tree;
    }

    private boolean containsKeyRecursive(BSTMap<K, V> tree, K key) {
        if (tree == null || tree.map == null) {
            return false;
        }
        int cmp = key.compareTo(tree.map.getKey());
        if (cmp == 0) {
            return true;
        } else if (cmp < 0) {
            return containsKeyRecursive(tree.left, key);
        } else {
            return containsKeyRecursive(tree.right, key);
        }
    }

    private int sizeRecursive(BSTMap<K, V> tree) {
        if (tree == null) {
            return 0;
        }
        if (tree.map == null) {
            return sizeRecursive(tree.left) + sizeRecursive(tree.right);
        } else {
            return sizeRecursive(tree.left) + sizeRecursive(tree.right) + 1;
        }
    }

    private static class MapNode<K, V> {
        private final K key;
        private final V value;

        MapNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}

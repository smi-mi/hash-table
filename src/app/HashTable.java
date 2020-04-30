package app;

import static java.lang.Math.abs;

public class HashTable<K, V> {
    private static final int STEP = 701;
    private static final double DEFAULT_LOAD_FACTOR = 0.5;
    private static final int RESIZE_FACTOR = 2;
    private static final double EPSILON = 1e-5;

    private Entry<K, V>[] table;
    private int keysNum = 0;
    private int removedKeysNum = 0;
    private int capacity;
    private double loadFactor;

    public HashTable(int capacity, double loadFactor) {
        this.table = new Entry[capacity];
        this.capacity = capacity;
        this.loadFactor = loadFactor;
    }

    public HashTable(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    V put(K key, V value) {
        checkAndResizeIfNeeded();
        int index = findIndex(key);
        if (table[index] == null) {
            table[index] = new Entry<>(key, value);
            ++keysNum;
            return null;
        }
        V lastValue = table[index].value;
        table[index].value = value;
        return lastValue;
    }

    V get(K key) {
        int index = findIndex(key);
        if (table[index] == null) {
            return null;
        }
        return table[index].value;
    }

    V remove(K key) {
        int index = findIndex(key);
        if (table[index] == null) {
            return null;
        }
        V lastValue = table[index].value;
        table[index] = new Entry<>();
        --keysNum;
        ++removedKeysNum;
        return lastValue;
    }

    int size() {
        return keysNum;
    }

    private int findIndex(K key) {
        int index = getInitIndex(key);
        while (table[index] != null) {
            if (key.equals(table[index].key)) {
                return index;
            }
            index = getNextIndex(index);
        }
        return index;
    }

    private int getInitIndex(K key) {
        return abs(key.hashCode() % capacity);
    }

    private int getNextIndex(int index) {
        return (index + STEP) % capacity;
    }

    private void checkAndResizeIfNeeded() {
        if ((double) (keysNum + removedKeysNum) / (double) capacity - loadFactor > EPSILON) {
            resize(1);
        }
        if ((double) (keysNum) / (double) capacity - loadFactor > EPSILON) {
            resize(RESIZE_FACTOR);
        }
    }

    private void resize(int resizeFactor) {
        Entry<K, V>[] oldTable = table;

        table = new Entry[capacity * resizeFactor];
        capacity *= resizeFactor;
        keysNum = 0;
        removedKeysNum = 0;

        for (Entry<K, V> entry : oldTable) {
            if (entry != null && entry.key != null) {
                put(entry.key, entry.value);
            }
        }
    }

    private static class Entry<K, V> {
        private K key;
        private V value;

        public Entry() {
            this.key = null;
            this.value = null;
        }

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}

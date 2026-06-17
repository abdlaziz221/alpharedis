package fr.redis.core;

import java.util.ArrayList;
import java.util.List;

public class HashTable {
    private final DynamicArray<Entry> table;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;

    public HashTable() {
        this.table = new DynamicArray<>(DEFAULT_CAPACITY);
        for (int i = 0; i < DEFAULT_CAPACITY; i++) {
            table.add(null);
        }
        this.size = 0;
    }

    public HashTable(int capacity) {
        this.table = new DynamicArray<>(capacity);
        for (int i = 0; i < capacity; i++) {
            table.add(null);
        }
        this.size = 0;
    }

    private int getBucket(String key) {
        return HashFunction.hash(key, table.size());
    }

    public void put(String key, String value) {
        int index = getBucket(key);
        Entry current = table.get(index);

        while (current != null) {
            if (current.getKey().equals(key)) {
                current.setStringValue(value);
                return;
            }
            current = current.getNext();
        }

        Entry newEntry = new Entry(key, value);
        newEntry.setNext(table.get(index));
        table.set(index, newEntry);
        size++;
    }

    public String get(String key) {
        int index = getBucket(key);
        Entry current = table.get(index);

        while (current != null) {
            if (current.getKey().equals(key)) {
                if (current.getType() == Entry.EntryType.STRING) {
                    return current.getStringValue();
                } else {
                    return null;
                }
            }
            current = current.getNext();
        }
        return null;
    }

    public Entry getEntry(String key) {
        int index = getBucket(key);
        Entry current = table.get(index);

        while (current != null) {
            if (current.getKey().equals(key)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    public boolean remove(String key) {
        int index = getBucket(key);
        Entry current = table.get(index);
        Entry prev = null;

        while (current != null) {
            if (current.getKey().equals(key)) {
                if (prev == null) {
                    table.set(index, current.getNext());
                } else {
                    prev.setNext(current.getNext());
                }
                size--;
                return true;
            }
            prev = current;
            current = current.getNext();
        }
        return false;
    }

    public boolean contains(String key) {
        return getEntry(key) != null;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public List<String> keys() {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            Entry current = table.get(i);
            while (current != null) {
                keys.add(current.getKey());
                current = current.getNext();
            }
        }
        return keys;
    }

    public void clear() {
        for (int i = 0; i < table.size(); i++) {
            table.set(i, null);
        }
        size = 0;
    }
}

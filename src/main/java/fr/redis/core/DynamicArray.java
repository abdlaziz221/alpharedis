package fr.redis.core;

import java.util.Arrays;

public class DynamicArray<T> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double GROW_FACTOR = 2.0;

    @SuppressWarnings("unchecked")
    private T[] data = (T[]) new Object[DEFAULT_CAPACITY];
    private int size = 0;

    @SuppressWarnings("unchecked")
    public DynamicArray() {
        data = (T[]) new Object[DEFAULT_CAPACITY];
    }

    @SuppressWarnings("unchecked")
    public DynamicArray(int initialCapacity) {
        data = (T[]) new Object[Math.max(initialCapacity, 1)];
    }

    public void set(int index, T element) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        data[index] = element;
    }

    public T get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        return data[index];
    }

    @SuppressWarnings("unchecked")
    public void add(T element) {
        if (size == data.length) {
            data = Arrays.copyOf(data, (int) (data.length * GROW_FACTOR));
        }
        data[size++] = element;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}

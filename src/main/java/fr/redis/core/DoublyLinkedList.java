package fr.redis.core;

public class DoublyLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
        }
        size++;
    }

    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.setPrev(tail);
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    public T removeFirst() {
        if (isEmpty()) return null;
        T data = head.getData();
        if (head == tail) {
            head = tail = null;
        } else {
            head = head.getNext();
            head.setPrev(null);
        }
        size--;
        return data;
    }

    public T removeLast() {
        if (isEmpty()) return null;
        T data = tail.getData();
        if (head == tail) {
            head = tail = null;
        } else {
            tail = tail.getPrev();
            tail.setNext(null);
        }
        size--;
        return data;
    }

    public T getFirst() { return isEmpty() ? null : head.getData(); }
    public T getLast() { return isEmpty() ? null : tail.getData(); }
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public Node<T> getHead() { return head; }
    public Node<T> getTail() { return tail; }
}

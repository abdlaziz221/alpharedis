package fr.redis.core;

public class Entry {
    private final String key;
    private EntryType type;
    private String stringValue;
    private DoublyLinkedList<String> listValue;
    private Entry next;

    public enum EntryType {
        STRING, LIST
    }

    public Entry(String key, String value) {
        this.key = key;
        this.type = EntryType.STRING;
        this.stringValue = value;
        this.listValue = null;
        this.next = null;
    }

    public Entry(String key) {
        this.key = key;
        this.type = EntryType.LIST;
        this.stringValue = null;
        this.listValue = new DoublyLinkedList<>();
        this.next = null;
    }

    public String getKey() { return key; }
    public EntryType getType() { return type; }

    public String getStringValue() { return stringValue; }
    public void setStringValue(String value) {
        this.stringValue = value;
        this.type = EntryType.STRING;
        this.listValue = null;
    }

    public DoublyLinkedList<String> getListValue() { return listValue; }
    public void setListValue(DoublyLinkedList<String> list) {
        this.listValue = list;
        this.type = EntryType.LIST;
        this.stringValue = null;
    }

    public void convertToList() {
        this.listValue = new DoublyLinkedList<>();
        if (this.stringValue != null) {
            this.listValue.addLast(this.stringValue);
        }
        this.stringValue = null;
        this.type = EntryType.LIST;
    }

    public Entry getNext() { return next; }
    public void setNext(Entry next) { this.next = next; }
}

package fr.redis.engine;

import fr.redis.core.Entry;
import fr.redis.core.HashTable;
import fr.redis.core.DoublyLinkedList;

import java.util.List;

public class CommandExecutor {
    private final HashTable db;

    public CommandExecutor() {
        this.db = new HashTable();
    }

    public CommandExecutor(HashTable db) {
        this.db = db;
    }

    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "ERR empty command";
        }

        String[] parts = input.trim().split("\\s+");
        String cmd = parts[0].toUpperCase();

        switch (cmd) {
            case "SET": return cmdSet(parts);
            case "GET": return cmdGet(parts);
            case "DEL": return cmdDel(parts);
            case "LPUSH": return cmdLpush(parts);
            case "RPUSH": return cmdRpush(parts);
            case "LPOP": return cmdLpop(parts);
            case "RPOP": return cmdRpop(parts);
            case "KEYS": return cmdKeys();
            case "TYPE": return cmdType(parts);
            case "LRANGE": return cmdLrange(parts);
            case "FLUSHALL": return cmdFlushall();
            case "EXIT": return "EXIT";
            default:
                return "ERR unknown command '" + cmd + "'";
        }
    }

    private String cmdSet(String[] parts) {
        if (parts.length < 3) {
            return "ERR wrong number of arguments for 'SET' command";
        }
        db.put(parts[1], parts[2]);
        return "OK";
    }

    private String cmdGet(String[] parts) {
        if (parts.length < 2) {
            return "ERR wrong number of arguments for 'GET' command";
        }
        String value = db.get(parts[1]);
        if (value == null) {
            Entry entry = db.getEntry(parts[1]);
            if (entry != null && entry.getType() == Entry.EntryType.LIST) {
                return "(error) WRONGTYPE Operation against a key holding the wrong kind of value";
            }
            return "(nil)";
        }
        return "\"" + value + "\"";
    }

    private String cmdDel(String[] parts) {
        if (parts.length < 2) {
            return "ERR wrong number of arguments for 'DEL' command";
        }
        boolean removed = db.remove(parts[1]);
        return removed ? "(integer) 1" : "(integer) 0";
    }

    private String cmdLpush(String[] parts) {
        if (parts.length < 3) {
            return "ERR wrong number of arguments for 'LPUSH' command";
        }
        Entry entry = db.getEntry(parts[1]);
        if (entry == null) {
            entry = new Entry(parts[1]);
            db.put(parts[1], null);
            Entry newEntry = db.getEntry(parts[1]);
            newEntry.convertToList();
            newEntry.getListValue().addFirst(parts[2]);
            return "(integer) " + newEntry.getListValue().size();
        }
        if (entry.getType() == Entry.EntryType.STRING) {
            entry.convertToList();
        }
        entry.getListValue().addFirst(parts[2]);
        return "(integer) " + entry.getListValue().size();
    }

    private String cmdRpush(String[] parts) {
        if (parts.length < 3) {
            return "ERR wrong number of arguments for 'RPUSH' command";
        }
        Entry entry = db.getEntry(parts[1]);
        if (entry == null) {
            db.put(parts[1], null);
            Entry newEntry = db.getEntry(parts[1]);
            newEntry.convertToList();
            newEntry.getListValue().addLast(parts[2]);
            return "(integer) " + newEntry.getListValue().size();
        }
        if (entry.getType() == Entry.EntryType.STRING) {
            entry.convertToList();
        }
        entry.getListValue().addLast(parts[2]);
        return "(integer) " + entry.getListValue().size();
    }

    private String cmdLpop(String[] parts) {
        if (parts.length < 2) {
            return "ERR wrong number of arguments for 'LPOP' command";
        }
        Entry entry = db.getEntry(parts[1]);
        if (entry == null || entry.getType() != Entry.EntryType.LIST || entry.getListValue().isEmpty()) {
            return "(nil)";
        }
        String value = entry.getListValue().removeFirst();
        if (entry.getListValue().isEmpty()) {
            db.remove(parts[1]);
        }
        return "\"" + value + "\"";
    }

    private String cmdRpop(String[] parts) {
        if (parts.length < 2) {
            return "ERR wrong number of arguments for 'RPOP' command";
        }
        Entry entry = db.getEntry(parts[1]);
        if (entry == null || entry.getType() != Entry.EntryType.LIST || entry.getListValue().isEmpty()) {
            return "(nil)";
        }
        String value = entry.getListValue().removeLast();
        if (entry.getListValue().isEmpty()) {
            db.remove(parts[1]);
        }
        return "\"" + value + "\"";
    }

    private String cmdKeys() {
        List<String> keys = db.keys();
        if (keys.isEmpty()) {
            return "(empty array)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            sb.append((i + 1) + ") \"").append(keys.get(i)).append("\"");
            if (i < keys.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }

    private String cmdType(String[] parts) {
        if (parts.length < 2) {
            return "ERR wrong number of arguments for 'TYPE' command";
        }
        Entry entry = db.getEntry(parts[1]);
        if (entry == null) {
            return "none";
        }
        return entry.getType() == Entry.EntryType.STRING ? "string" : "list";
    }

    private String cmdLrange(String[] parts) {
        if (parts.length < 4) {
            return "ERR wrong number of arguments for 'LRANGE' command";
        }
        Entry entry = db.getEntry(parts[1]);
        if (entry == null) {
            return "(empty array)";
        }
        if (entry.getType() != Entry.EntryType.LIST) {
            return "(error) WRONGTYPE Operation against a key holding the wrong kind of value";
        }
        DoublyLinkedList<String> list = entry.getListValue();
        int start = Integer.parseInt(parts[2]);
        int stop = Integer.parseInt(parts[3]);

        if (start < 0) start = Math.max(list.size() + start, 0);
        if (stop < 0) stop = list.size() + stop;
        if (start > stop || start >= list.size()) {
            return "(empty array)";
        }
        stop = Math.min(stop, list.size() - 1);

        StringBuilder sb = new StringBuilder();
        int idx = 0;
        fr.redis.core.Node<String> current = list.getHead();
        for (int i = 0; i < start && current != null; i++) {
            current = current.getNext();
        }
        for (int i = start; i <= stop && current != null; i++) {
            sb.append((idx + 1) + ") \"").append(current.getData()).append("\"");
            if (i < stop) sb.append("\n");
            current = current.getNext();
            idx++;
        }
        return sb.toString();
    }

    private String cmdFlushall() {
        db.clear();
        return "OK";
    }

    public HashTable getDb() { return db; }
}

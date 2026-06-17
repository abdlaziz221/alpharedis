package fr.redis.core;

public class HashFunction {

    public static long djb2(String key) {
        long hash = 5381;
        for (int i = 0; i < key.length(); i++) {
            hash = ((hash << 5) + hash) + key.charAt(i);
            hash &= 0xFFFFFFFFL;
        }
        return hash;
    }

    public static long murmurHash3(String key) {
        final int seed = 0x9747b28c;
        byte[] data = key.getBytes();
        int length = data.length;
        int h = seed;
        int i = 0;

        while (length >= 4) {
            int k = (data[i] & 0xFF)
                  | ((data[i + 1] & 0xFF) << 8)
                  | ((data[i + 2] & 0xFF) << 16)
                  | ((data[i + 3] & 0xFF) << 24);

            k *= 0xcc9e2d51;
            k = Integer.rotateLeft(k, 15);
            k *= 0x1b873593;

            h ^= k;
            h = Integer.rotateLeft(h, 13);
            h = h * 5 + 0xe6546b64;

            i += 4;
            length -= 4;
        }

        int k = 0;
        switch (length) {
            case 3: k ^= (data[i + 2] & 0xFF) << 16;
            case 2: k ^= (data[i + 1] & 0xFF) << 8;
            case 1: k ^= (data[i] & 0xFF);
                    k *= 0xcc9e2d51;
                    k = Integer.rotateLeft(k, 15);
                    k *= 0x1b873593;
                    h ^= k;
        }

        h ^= data.length;
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;

        return h & 0xFFFFFFFFL;
    }

    public static int hash(String key, int tableSize) {
        long hashValue = djb2(key);
        return (int) (Math.abs(hashValue) % tableSize);
    }

    public static int hash(String key, int tableSize, HashAlgo algo) {
        long hashValue;
        if (algo == HashAlgo.MURMUR) {
            hashValue = murmurHash3(key);
        } else {
            hashValue = djb2(key);
        }
        return (int) (Math.abs(hashValue) % tableSize);
    }

    public enum HashAlgo {
        DJB2, MURMUR
    }
}

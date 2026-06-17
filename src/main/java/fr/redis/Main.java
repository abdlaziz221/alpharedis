package fr.redis;

import fr.redis.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}

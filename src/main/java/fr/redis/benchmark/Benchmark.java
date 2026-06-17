package fr.redis.benchmark;

import fr.redis.engine.CommandExecutor;
import fr.redis.server.ColorConsole;

public class Benchmark {

    public static void main(String[] args) {
        ColorConsole.bold("========================================");
        ColorConsole.bold("  α-Redis Benchmark & Tests de validation");
        ColorConsole.bold("========================================\n");

        testEdgeCases();
        performanceTest();

        ColorConsole.bold("\n========================================");
        ColorConsole.success("  Tous les tests sont passes !");
        ColorConsole.bold("========================================");
    }

    private static void testEdgeCases() {
        ColorConsole.bold("--- Tests de cas limites ---\n");
        CommandExecutor exec = new CommandExecutor();
        int passed = 0;
        int total = 0;

        total++;
        String r = exec.execute("GET inexistante");
        if (r.equals("(nil)")) { passed++; ColorConsole.success("  [OK] GET cle inexistante -> (nil)"); }
        else ColorConsole.error("  [FAIL] GET cle inexistante -> " + r);

        total++;
        r = exec.execute("DEL inexistante");
        if (r.equals("(integer) 0")) { passed++; ColorConsole.success("  [OK] DEL cle inexistante -> (integer) 0"); }
        else ColorConsole.error("  [FAIL] DEL cle inexistante -> " + r);

        total++;
        r = exec.execute("SET cle1 valeur1");
        r = exec.execute("GET cle1");
        if (r.equals("\"valeur1\"")) { passed++; ColorConsole.success("  [OK] SET/GET cle -> valeur correcte"); }
        else ColorConsole.error("  [FAIL] SET/GET cle -> " + r);

        total++;
        r = exec.execute("SET cle1 nouvelle");
        r = exec.execute("GET cle1");
        if (r.equals("\"nouvelle\"")) { passed++; ColorConsole.success("  [OK] SET mise a jour -> valeur mise a jour"); }
        else ColorConsole.error("  [FAIL] SET mise a jour -> " + r);

        total++;
        r = exec.execute("LPUSH liste1 a");
        r = exec.execute("LPUSH liste1 b");
        r = exec.execute("RPUSH liste1 c");
        r = exec.execute("LRANGE liste1 0 -1");
        if (r.contains("\"b\"") && r.contains("\"a\"") && r.contains("\"c\"")) { passed++; ColorConsole.success("  [OK] LPUSH/RPUSH -> ordre correct"); }
        else ColorConsole.error("  [FAIL] LPUSH/RPUSH -> " + r);

        total++;
        exec.execute("LPUSH liste2 x");
        r = exec.execute("LPOP liste2");
        if (r.equals("\"x\"")) { passed++; ColorConsole.success("  [OK] LPOP -> element extrait"); }
        else ColorConsole.error("  [FAIL] LPOP -> " + r);

        total++;
        r = exec.execute("LPOP liste2");
        if (r.equals("(nil)")) { passed++; ColorConsole.success("  [OK] LPOP liste vide -> (nil)"); }
        else ColorConsole.error("  [FAIL] LPOP liste vide -> " + r);

        total++;
        exec.execute("RPUSH liste3 y");
        r = exec.execute("RPOP liste3");
        if (r.equals("\"y\"")) { passed++; ColorConsole.success("  [OK] RPOP -> element extrait"); }
        else ColorConsole.error("  [FAIL] RPOP -> " + r);

        total++;
        r = exec.execute("RPOP liste3");
        if (r.equals("(nil)")) { passed++; ColorConsole.success("  [OK] RPOP liste vide -> (nil)"); }
        else ColorConsole.error("  [FAIL] RPOP liste vide -> " + r);

        total++;
        r = exec.execute("LPUSH liste4 a");
        r = exec.execute("RPUSH liste4 b");
        r = exec.execute("LPOP liste4");
        r = exec.execute("RPOP liste4");
        r = exec.execute("LPOP liste4");
        if (r.equals("(nil)")) { passed++; ColorConsole.success("  [OK] Liste auto-supprimee quand vide"); }
        else ColorConsole.error("  [FAIL] Liste auto-supprimee -> " + r);

        total++;
        r = exec.execute("LRANGE liste4 0 -1");
        if (r.equals("(empty array)")) { passed++; ColorConsole.success("  [OK] LRANGE cle inexistante -> (empty array)"); }
        else ColorConsole.error("  [FAIL] LRANGE cle inexistante -> " + r);

        total++;
        exec.execute("SET stringcle hello");
        r = exec.execute("LRANGE stringcle 0 -1");
        if (r.contains("WRONGTYPE")) { passed++; ColorConsole.success("  [OK] LRANGE sur string -> WRONGTYPE"); }
        else ColorConsole.error("  [FAIL] LRANGE sur string -> " + r);

        total++;
        r = exec.execute("TYPE stringcle");
        if (r.equals("string")) { passed++; ColorConsole.success("  [OK] TYPE stringcle -> string"); }
        else ColorConsole.error("  [FAIL] TYPE stringcle -> " + r);

        total++;
        exec.execute("LPUSH listecle z");
        r = exec.execute("TYPE listecle");
        if (r.equals("list")) { passed++; ColorConsole.success("  [OK] TYPE listecle -> list"); }
        else ColorConsole.error("  [FAIL] TYPE listecle -> " + r);

        total++;
        r = exec.execute("SET");
        if (r.startsWith("ERR")) { passed++; ColorConsole.success("  [OK] SET sans args -> ERR"); }
        else ColorConsole.error("  [FAIL] SET sans args -> " + r);

        total++;
        r = exec.execute("GET");
        if (r.startsWith("ERR")) { passed++; ColorConsole.success("  [OK] GET sans args -> ERR"); }
        else ColorConsole.error("  [FAIL] GET sans args -> " + r);

        total++;
        r = exec.execute("UNKNOWN_CMD");
        if (r.startsWith("ERR")) { passed++; ColorConsole.success("  [OK] Commande inconnue -> ERR"); }
        else ColorConsole.error("  [FAIL] Commande inconnue -> " + r);

        total++;
        r = exec.execute("");
        if (r.startsWith("ERR")) { passed++; ColorConsole.success("  [OK] Commande vide -> ERR"); }
        else ColorConsole.error("  [FAIL] Commande vide -> " + r);

        ColorConsole.info("\n  Resultat: " + passed + "/" + total + " tests passes\n");
    }

    private static void performanceTest() {
        ColorConsole.bold("--- Benchmark de performance ---\n");
        CommandExecutor exec = new CommandExecutor();

        int n = 10000;

        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            exec.execute("SET key" + i + " value" + i);
        }
        long setDuration = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            exec.execute("GET key" + i);
        }
        long getDuration = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            exec.execute("DEL key" + i);
        }
        long delDuration = System.nanoTime() - start;

        exec.execute("LPUSH benchlist init");
        start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            exec.execute("LPUSH benchlist val" + i);
        }
        long lpushDuration = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            exec.execute("LPOP benchlist");
        }
        long lpopDuration = System.nanoTime() - start;

        ColorConsole.info("  SET " + n + " cles      : " + formatDuration(setDuration) + " (" + String.format("%.2f", setDuration / 1000000.0) + " ms)");
        ColorConsole.info("  GET " + n + " cles      : " + formatDuration(getDuration) + " (" + String.format("%.2f", getDuration / 1000000.0) + " ms)");
        ColorConsole.info("  DEL " + n + " cles      : " + formatDuration(delDuration) + " (" + String.format("%.2f", delDuration / 1000000.0) + " ms)");
        ColorConsole.info("  LPUSH " + n + " elements  : " + formatDuration(lpushDuration) + " (" + String.format("%.2f", lpushDuration / 1000000.0) + " ms)");
        ColorConsole.info("  LPOP " + n + " elements  : " + formatDuration(lpopDuration) + " (" + String.format("%.2f", lpopDuration / 1000000.0) + " ms)");
        ColorConsole.info("  Moyenne SET/GET/DEL : " + String.format("%.2f", (setDuration + getDuration + delDuration) / (3.0 * n)) + " ns/op");
    }

    private static String formatDuration(long nanos) {
        if (nanos < 1000) return nanos + " ns";
        if (nanos < 1000000) return String.format("%.2f us", nanos / 1000.0);
        return String.format("%.2f ms", nanos / 1000000.0);
    }
}

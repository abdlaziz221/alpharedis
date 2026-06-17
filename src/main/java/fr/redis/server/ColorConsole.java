package fr.redis.server;

public class ColorConsole {

    public static final String RESET   = "\u001B[0m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";
    public static final String BOLD    = "\u001B[1m";
    public static final String DIM     = "\u001B[2m";

    public static void success(String msg) {
        System.out.println(GREEN + msg + RESET);
    }

    public static void error(String msg) {
        System.out.println(RED + msg + RESET);
    }

    public static void info(String msg) {
        System.out.println(CYAN + msg + RESET);
    }

    public static void warning(String msg) {
        System.out.println(YELLOW + msg + RESET);
    }

    public static void dim(String msg) {
        System.out.println(DIM + msg + RESET);
    }

    public static void bold(String msg) {
        System.out.println(BOLD + msg + RESET);
    }

    public static void printPrompt() {
        System.out.print(CYAN + BOLD + "REDISLIKE> " + RESET);
    }

    public static void printWelcome() {
        String ascii = RED + BOLD +
            "######  ####### ######  ###  #####  #       ### #    # ####### \n" +
            "#     # #       #     #  #  #     # #        #  #   #  #       \n" +
            "#     # #       #     #  #  #       #        #  #  #   #       \n" +
            "######  #####   #     #  #   #####  #        #  ###    #####   \n" +
            "#   #   #       #     #  #        # #        #  #  #   #       \n" +
            "#    #  #       #     #  #  #     # #        #  #   #  #       \n" +
            "#     # ####### ######  ###  #####  ####### ### #    # ####### \n" +
            RESET;
        System.out.println(ascii);
        System.out.println(DIM + "  Moteur de stockage NoSQL In-Memory v1.0" + RESET);
        System.out.println(DIM + "  Tapez 'EXIT' pour quitter, 'HELP' pour l'aide" + RESET);
        System.out.println();
    }

    public static void printHelp() {
        System.out.println(BOLD + "\nCommandes disponibles :\n" + RESET);
        System.out.println(GREEN + "  SET key value" + DIM + "          - Ajouter/mettre a jour une cle" + RESET);
        System.out.println(GREEN + "  GET key" + DIM + "               - Recuperer la valeur d'une cle" + RESET);
        System.out.println(GREEN + "  DEL key" + DIM + "               - Supprimer une cle" + RESET);
        System.out.println();
        System.out.println(GREEN + "  LPUSH key value" + DIM + "        - Inserer en tete de liste" + RESET);
        System.out.println(GREEN + "  RPUSH key value" + DIM + "        - Inserer en queue de liste" + RESET);
        System.out.println(GREEN + "  LPOP key" + DIM + "              - Extraire en tete" + RESET);
        System.out.println(GREEN + "  RPOP key" + DIM + "              - Extraire en queue" + RESET);
        System.out.println(GREEN + "  LRANGE key start stop" + DIM + "  - Afficher une sous-liste" + RESET);
        System.out.println();
        System.out.println(GREEN + "  KEYS" + DIM + "                  - Lister toutes les cles" + RESET);
        System.out.println(GREEN + "  TYPE key" + DIM + "               - Type d'une cle (string/list)" + RESET);
        System.out.println(GREEN + "  FLUSHALL" + DIM + "               - Supprimer toutes les cles" + RESET);
        System.out.println(GREEN + "  HELP" + DIM + "                  - Afficher cette aide" + RESET);
        System.out.println(GREEN + "  EXIT" + DIM + "                  - Quitter le serveur" + RESET);
        System.out.println();
    }
}

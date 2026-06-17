#!/bin/bash
echo ""
echo "  Compilation en cours..."
echo ""

mkdir -p target/classes

javac -encoding UTF-8 -d target/classes -sourcepath src/main/java \
    src/main/java/fr/redis/Main.java \
    src/main/java/fr/redis/core/*.java \
    src/main/java/fr/redis/engine/*.java \
    src/main/java/fr/redis/server/*.java

if [ $? -ne 0 ]; then
    echo ""
    echo "  [ERREUR] Compilation echouee !"
    exit 1
fi

echo "  Compilation reussie !"
echo ""
java -cp target/classes fr.redis.Main

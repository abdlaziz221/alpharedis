# REDISLIKE

### Moteur de stockage NoSQL In-Memory — Projet Académique

---

## Table des matières

1. [Présentation du projet](#1-présentation-du-projet)
2. [Objectifs pédagogiques](#2-objectifs-pédagogiques)
3. [Architecture technique](#3-architecture-technique)
4. [Structures de données utilisées](#4-structures-de-données-utilisées)
5. [Fonction de hachage](#5-fonction-de-hachage)
6. [Table de hachage et résolution des collisions](#6-table-de-hachage-et-résolution-des-collisions)
7. [API — Commandes disponibles](#7-api--commandes-disponibles)
8. [Installation et exécution](#8-installation-et-exécution)
9. [Guide d'utilisation](#9-guide-dutilisation)
10. [Structure du projet](#10-structure-du-projet)

---

## 1. Présentation du projet

**REDISLIKE** est un moteur de stockage de données de type **Clé-Valeur** (Key-Value), inspiré de [Redis](https://redis.io/), développé à partir de zéro en **Java** sans utiliser les collections de la bibliothèque standard (`HashMap`, `ArrayList`, etc.).

Ce projet est réalisé dans le cadre du cours **Algorithmes et Structures de Données (ASD)**.

### Caractéristiques

| Propriété | Valeur |
|-----------|--------|
| Type | NoSQL In-Memory |
| Stockage | En mémoire (RAM) |
| Langage | Java 17 |
| Complexité d'accès | O(1) en moyenne |
| Structures utilisées | Tableau dynamique, Table de hachage, Listes chaînées |

---

## 2. Objectifs pédagogiques

Ce projet vise à manipuler et implémenter les concepts fondamentaux suivants :

- **Tableau dynamique** : allocation dynamique de mémoire, réallocation lors du dépassement de capacité
- **Liste doublement chaînée** : opérations en O(1) aux extrémités (insertion, suppression)
- **Fonction de hachage** : algorithme DJB2 et MurmurHash3
- **Table de hachage** : indexation par clé, résolution des collisions par chaînage séparé
- **Interpréteur de commandes** : parsing de commandes texte, dispatch vers les bonnes opérations

---

## 3. Architecture technique

```
┌─────────────────────────────────────────────────┐
│                   REPL (Console)                 │
│            Server.java / ColorConsole.java       │
├─────────────────────────────────────────────────┤
│              Moteur de commandes                 │
│               CommandExecutor.java               │
│    SET │ GET │ DEL │ LPUSH │ RPUSH │ LPOP │ ... │
├─────────────────────────────────────────────────┤
│           Couche d'indexation                    │
│              HashTable.java                      │
│         Chaînage séparé (collisions)            │
├─────────────────────────────────────────────────┤
│         Fonction de hachage                      │
│           HashFunction.java                      │
│              DJB2 / MurmurHash3                 │
├─────────────────────────────────────────────────┤
│          Structures de données                   │
│   DynamicArray.java  │  DoublyLinkedList.java   │
│       Node.java      │      Entry.java          │
└─────────────────────────────────────────────────┘
```

### Flux d'exécution

```
Utilisateur tape une commande
        │
        ▼
  Server (REPL) lit l'entrée
        │
        ▼
  CommandExecutor.parse()
        │
        ▼
  Dispatch vers la méthode correspondante
        │
        ▼
  HashTable opère la recherche/insertion
        │
        ▼
  Résultat affiché dans la console
```

---

## 4. Structures de données utilisées

### 4.1 Node\<T\> — Nœud générique

Chaque nœud contient :
- `data` : la donnée stockée
- `next` : pointeur vers le nœud suivant
- `prev` : pointeur vers le nœud précédent

```java
public class Node<T> {
    private T data;
    private Node<T> next;
    private Node<T> prev;
}
```

### 4.2 DoublyLinkedList\<T\> — Liste doublement chaînée

| Méthode | Description | Complexité |
|---------|-------------|------------|
| `addFirst(data)` | Insère en tête | O(1) |
| `addLast(data)` | Insère en queue | O(1) |
| `removeFirst()` | Supprime et retourne la tête | O(1) |
| `removeLast()` | Supprime et retourne la queue | O(1) |
| `getFirst()` | Retourne la tête sans supprimer | O(1) |
| `getLast()` | Retourne la queue sans supprimer | O(1) |
| `size()` | Retourne la taille | O(1) |
| `isEmpty()` | Vérifie si la liste est vide | O(1) |

**Pourquoi une liste doublement chaînée ?**
- Permet des insertions et suppressions en O(1) aux deux extrémités
- Nécessaire pour implémenter `LPUSH`, `RPUSH`, `LPOP`, `RPOP`

### 4.3 DynamicArray\<T\> — Tableau dynamique

| Méthode | Description | Complexité |
|---------|-------------|------------|
| `add(element)` | Ajoute un élément | O(1) amorti |
| `get(index)` | Accède par index | O(1) |
| `set(index, element)` | Remplace par index | O(1) |
| `size()` | Retourne la taille | O(1) |

**Gestion de la capacité :**
- Capacité initiale : 16
- Facteur de croissance : ×2
- Réallocation automatique quand le tableau est plein

### 4.4 Entry — Entrée de la table de hachage

Chaque entrée contient :
- `key` : la clé (String)
- `type` : `STRING` ou `LIST`
- `stringValue` : valeur si type STRING
- `listValue` : `DoublyLinkedList<String>` si type LIST
- `next` : pointeur vers l'entrée suivante (chaînage)

---

## 5. Fonction de hachage

### 5.1 DJB2

Algorithme créé par Daniel J. Bernstein. L'un des hachages les plus simples et efficaces.

```java
long hash = 5381;
for (int i = 0; i < key.length(); i++) {
    hash = ((hash << 5) + hash) + key.charAt(i);
    hash &= 0xFFFFFFFFL;
}
```

**Propriétés :**
- Multiplicateur : 33 (impliqué par `<< 5 + hash`)
- Initialisation : 5381
- Très bon distribution pour les chaînes de caractères

### 5.2 MurmurHash3

Algorithme de hachage non-cryptographique créé par Austin Appleby. Plus distribué que DJB2.

**Étapes principales :**
1. Traitement par blocs de 4 octets
2. Multiplication par `0xcc9e2d51`
3. Rotation gauche de 15 bits
4. Multiplication par `0x1b873593`
5. Mélange final (avalanche)

### 5.3 Utilisation

```java
// Hachage simple
int index = HashFunction.hash("cle", tableSize);

// Avec choix d'algorithme
int index = HashFunction.hash("cle", tableSize, HashAlgo.MURMUR);
```

---

## 6. Table de hachage et résolution des collisions

### Principe

```
Clé → HashFunction.hash(key, tableSize) → Index → Tableau[index] → Liste chaînée
```

### Résolution par chaînage séparé

Quand deux clés ont le même index de hachage (collision), elles sont stockées dans la même liste chaînée :

```
Index 0: → Entry("cle1") → Entry("cle2") → null
Index 1: → null
Index 2: → Entry("cle3") → null
Index 3: → Entry("cle4") → Entry("cle5") → Entry("cle6") → null
```

### Méthodes de la HashTable

| Méthode | Description | Complexité moyenne |
|---------|-------------|--------------------|
| `put(key, value)` | Insère ou met à jour | O(1) |
| `get(key)` | Recherche une clé | O(1) |
| `remove(key)` | Supprime une clé | O(1) |
| `contains(key)` | Vérifie l'existence | O(1) |
| `keys()` | Liste toutes les clés | O(n) |
| `clear()` | Vide la table | O(n) |

---

## 7. API — Commandes disponibles

### Commandes sur les chaînes

| Commande | Syntaxe | Description | Exemple |
|----------|---------|-------------|---------|
| `SET` | `SET key value` | Ajoute ou met à jour une clé | `SET nom Ibrahima` |
| `GET` | `GET key` | Retourne la valeur d'une clé | `GET nom` → `"Ibrahima"` |
| `DEL` | `DEL key` | Supprime une clé | `DEL nom` → `(integer) 1` |

### Commandes sur les listes

| Commande | Syntaxe | Description | Exemple |
|----------|---------|-------------|---------|
| `LPUSH` | `LPUSH key value` | Insère en tête de liste | `LPUSH fruits pomme` |
| `RPUSH` | `RPUSH key value` | Insère en queue de liste | `RPUSH fruits banane` |
| `LPOP` | `LPOP key` | Extrait et supprime en tête | `LPOP fruits` → `"pomme"` |
| `RPOP` | `RPOP key` | Extrait et supprime en queue | `RPOP fruits` → `"banane"` |
| `LRANGE` | `LRANGE key start stop` | Affiche une sous-liste | `LRANGE fruits 0 -1` |

### Commandes utilitaires

| Commande | Syntaxe | Description | Exemple |
|----------|---------|-------------|---------|
| `KEYS` | `KEYS` | Liste toutes les clés | `KEYS` |
| `TYPE` | `TYPE key` | Retourne le type (string/list) | `TYPE nom` → `string` |
| `FLUSHALL` | `FLUSHALL` | Supprime toutes les clés | `FLUSHALL` |
| `LOAD` | `LOAD fichier.txt` | Exécute les commandes d'un fichier | `LOAD demo.txt` |
| `HELP` | `HELP` | Affiche l'aide | `HELP` |
| `EXIT` | `EXIT` | Quitte le serveur | `EXIT` |

### Codes de retour

| Retour | Signification |
|--------|---------------|
| `OK` | Commande exécutée avec succès |
| `"valeur"` | Valeur retournée (chaîne) |
| `(integer) N` | Entier retourné (nombre d'éléments affectés) |
| `(nil)` | Clé inexistante ou liste vide |
| `(empty array)` | Liste de clés ou sous-liste vide |
| `ERR ...` | Erreur de syntaxe ou d'arguments |
| `(error) WRONGTYPE ...` | Type de clé inattendu |

---

## 8. Installation et exécution

### Prérequis

- **Java 17** ou supérieur
- **Windows**, **Linux** ou **macOS**

### Compilation et lancement

#### Windows

```cmd
# Double-cliquer sur run.bat
# Ou manuellement :
javac -encoding UTF-8 -d target\classes -sourcepath src\main\java src\main\java\fr\redis\*.java src\main\java\fr\redis\core\*.java src\main\java\fr\redis\engine\*.java src\main\java\fr\redis\server\*.java src\main\java\fr\redis\benchmark\*.java
java -cp target\classes fr.redis.Main
```

#### Linux / macOS

```bash
chmod +x run.sh
./run.sh
```

### Lancer le benchmark

```bash
java -cp target/classes fr.redis.benchmark.Benchmark
```

---

## 9. Guide d'utilisation

### Démarrage

```
$ java -cp target/classes fr.redis.Main

######  ####### ######  ###  #####  #       ### #    # #######
#     # #       #     #  #  #     # #        #  #   #  #
#     # #       #     #  #  #       #        #  #  #   #
######  #####   #     #  #   #####  #        #  ###    #####
#   #   #       #     #  #        # #        #  #  #   #
#    #  #       #     #  #  #     # #        #  #   #  #
#     # ####### ######  ###  #####  ####### ### #    # #######

  Moteur de stockage NoSQL In-Memory v1.0
  Tapez 'EXIT' pour quitter, 'HELP' pour l'aide

REDISLIKE>
```

### Exemple : manipuler des chaînes

```
REDISLIKE> SET ville Dakar
OK
REDISLIKE> SET pays Senegal
OK
REDISLIKE> GET ville
"Dakar"
REDISLIKE> SET ville Thiès
OK
REDISLIKE> GET ville
"Thiès"
REDISLIKE> DEL pays
(integer) 1
REDISLIKE> GET pays
(nil)
```

### Exemple : manipuler des listes

```
REDISLIKE> LPUSH fruits pomme
(integer) 1
REDISLIKE> LPUSH fruits banane
(integer) 2
REDISLIKE> RPUSH fruits kiwi
(integer) 3
REDISLIKE> LRANGE fruits 0 -1
1) "banane"
2) "pomme"
3) "kiwi"
REDISLIKE> LPOP fruits
"banane"
REDISLIKE> RPOP fruits
"kiwi"
REDISLIKE> LRANGE fruits 0 -1
1) "pomme"
```

### Exemple : charger un script

```
REDISLIKE> LOAD demo.txt
Chargement du fichier: demo.txt
  SET nom Ibrahima
OK
  LPUSH fruits pomme
(integer) 1
  ...
  29 commandes executees.
```

---

## 10. Structure du projet

```
REDISLIKE/
├── README.md                           # Ce fichier
├── run.bat                             # Script de lancement (Windows)
├── run.sh                              # Script de lancement (Linux/Mac)
├── build.bat                           # Script de compilation
├── demo.txt                            # Script de démonstration
├── projet Stockage.pdf                 # Sujet du projet
├── pom.xml                             # Configuration Maven
│
└── src/main/java/fr/redis/
    ├── Main.java                       # Point d'entrée
    │
    ├── core/                           # Structures de données
    │   ├── Node.java                   # Nœud générique
    │   ├── DoublyLinkedList.java       # Liste doublement chaînée
    │   ├── DynamicArray.java           # Tableau dynamique
    │   ├── HashFunction.java           # DJB2 / MurmurHash3
    │   ├── Entry.java                  # Entrée (clé, type, valeur)
    │   └── HashTable.java             # Table de hachage
    │
    ├── engine/                         # Moteur de commandes
    │   └── CommandExecutor.java        # Interpréteur
    │
    ├── server/                         # Console interactive
    │   ├── Server.java                 # Boucle REPL
    │   └── ColorConsole.java           # Affichage ANSI coloré
    │
    └── benchmark/                      # Tests
        └── Benchmark.java              # Validation + perf
```

---

*Projet réalisé dans le cadre du cours ASD — Date butoir : 05 Juillet 2026*

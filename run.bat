@echo off
title α-Redis - Moteur NoSQL In-Memory
echo.
echo   Compilation en cours...
echo.

if not exist "target\classes" mkdir "target\classes"

javac -encoding UTF-8 -d target\classes -sourcepath src\main\java src\main\java\fr\redis\Main.java src\main\java\fr\redis\core\*.java src\main\java\fr\redis\engine\*.java src\main\java\fr\redis\server\*.java

if %errorlevel% neq 0 (
    echo.
    echo   [ERREUR] Compilation echouee !
    pause
    exit /b 1
)

echo   Compilation reussie !
echo.
java -cp target\classes fr.redis.Main
pause

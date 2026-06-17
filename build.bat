@echo off
echo.
echo   === Build α-Redis ===
echo.

if not exist "target\classes" mkdir "target\classes"

echo   Compilation des sources...
javac -encoding UTF-8 -d target\classes -sourcepath src\main\java ^
    src\main\java\fr\redis\Main.java ^
    src\main\java\fr\redis\core\Node.java ^
    src\main\java\fr\redis\core\DoublyLinkedList.java ^
    src\main\java\fr\redis\core\DynamicArray.java ^
    src\main\java\fr\redis\core\HashFunction.java ^
    src\main\java\fr\redis\core\Entry.java ^
    src\main\java\fr\redis\core\HashTable.java ^
    src\main\java\fr\redis\engine\CommandExecutor.java ^
    src\main\java\fr\redis\server\ColorConsole.java ^
    src\main\java\fr\redis\server\Server.java

if %errorlevel% neq 0 (
    echo.
    echo   [ERREUR] Compilation echouee !
    pause
    exit /b 1
)

echo.
echo   Build termine avec succes !
echo   Executable: target\classes\fr\redis\Main.class
echo.
pause

@echo off
echo ========================================================
echo Compiling AutoNet-Pi Sovereign Automotive Mesh (Java 21)
echo ========================================================

if not exist bin mkdir bin

dir /s /b src\main\java\*.java > sources.txt
dir /s /b src\test\java\*.java >> sources.txt

javac -d bin @sources.txt
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed.
    del sources.txt
    exit /b %ERRORLEVEL%
)

del sources.txt
echo [SUCCESS] Compilation complete. Running verification test suite...
java -cp bin io.autonet.pi.AutoNetTestSuite
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Test suite failed.
    exit /b %ERRORLEVEL%
)

echo.
echo Launching AutoNet-Pi Terminal HUD Demo...
java -cp bin io.autonet.pi.Main demo

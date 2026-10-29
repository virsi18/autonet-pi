#!/usr/bin/env bash
set -e

echo "========================================================"
echo "Compiling AutoNet-Pi Sovereign Automotive Mesh (Java 21)"
echo "========================================================"

mkdir -p bin
find src/main/java -name "*.java" > sources.txt
find src/test/java -name "*.java" >> sources.txt

javac -d bin @sources.txt
rm sources.txt

echo "[SUCCESS] Compilation complete. Running verification test suite..."
java -cp bin io.autonet.pi.AutoNetTestSuite

echo ""
echo "Launching AutoNet-Pi Terminal HUD Demo..."
java -cp bin io.autonet.pi.Main demo

#!/bin/bash
# usage: tools/shots.sh <outDir> [nameFilter] [mut] [form]   (needs ./gradlew classes printCp first; classpath cached in build/cp.txt)
cd "$(dirname "$0")/.." || exit 1
[ -f build/cp.txt ] || ./gradlew -q printCp | grep '^CP=' | sed 's/^CP=//' > build/cp.txt
CP="$(cat build/cp.txt):build/classes/java/main"
mkdir -p build/tools && javac -nowarn -XDignore.symbol.file -cp "$CP" -d build/tools tools/ModelShot.java && java -Djava.awt.headless=true -cp "$CP:build/tools" ModelShot "$@"

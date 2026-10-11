#!/bin/bash
# usage: jc.sh  -> compiles ALL sources except ModEntities/ClientSetup-dependent breakage is reported per file
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
cd /home/user/resident-evil
OUT=$(mktemp -d)
$JAVA_HOME/bin/javac -proc:none -nowarn -Xmaxerrs 500 -cp "$(cat /tmp/gr/cp.txt)" -d $OUT $(find src/main/java -name '*.java' "$@") 2>&1 | grep -v JAVA_TOOL | sed 's|src/main/java/com/reevil/||'
rm -rf $OUT

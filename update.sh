#!/bin/bash

mvn clean install
mvn install -f pom.xml
cp ./target/Quake2-1.0-SNAPSHOT.jar /home/minecraft/servers/quake-test/plugins
screen -S quake_test -p 0 -X stuff "reload confirm $(printf \\r)"

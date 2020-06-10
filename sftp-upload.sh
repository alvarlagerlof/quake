#!/bin/bash

mvn clean install
mvn install -f pom.xml
sftp minecraft@ravla.org:/home/minecraft/servers/quake-test/plugins/ <<< $'put ./target/Quake2-1.0-SNAPSHOT.jar'
ssh minecraft@ravla.org 'screen -S quake_test -p 0 -X stuff "reload confirm $(printf \\r)"'

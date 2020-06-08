#!/bin/bash

mvn clean install
mvn install -f pom.xml
cp ./target/Quake-1.0-SNAPSHOT.jar /home/minecraft/servers/bungee/servers/mini_quake/plugins/
screen -S bungee_mini_quake -p 0 -X stuff "reload confirm $(printf \\r)"

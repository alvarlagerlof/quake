#!/bin/bash

mvn install -f pom.xml
sftp minecraft@ravla.org:/home/minecraft/servers/bungee/servers/mini_quake/plugins/ <<< $'put ./target/Quake-1.0-SNAPSHOT.jar'
ssh minecraft@ravla.org 'screen -S bungee_mini_quake -p 0 -X stuff "reload confirm $(printf \\r)"'
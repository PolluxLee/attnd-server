#!/usr/bin/env bash
mvn clean
mvn package
ls /app/target/
java -Djava.security.egd=file:/dev/./urandom -jar /app/target/attnd.jar

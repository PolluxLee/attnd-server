#!/usr/bin/env bash
mvn clean package -Dmaven.test.skip=true
ls /app/target/
java -Djava.security.egd=file:/dev/./urandom -jar /app/target/attnd.jar

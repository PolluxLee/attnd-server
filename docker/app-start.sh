#!/usr/bin/env bash
mvn clean
if [ "$TEST_SW" = "off" ]
then
mvn package -Dmaven.test.skip=true
else
mvn package
fi
ls /app/target/
java -Djava.security.egd=file:/dev/./urandom -jar /app/target/attnd.jar

#!/usr/bin/env bash
LOG=/app/docker
mvn test > ${LOG}/testLog 2>&1
echo 'Test OK'
mvn package -Dmaven.test.skip=true > ${LOG}/packageLog 2>&1
echo 'Package OK'


#!/usr/bin/env bash
LOG=/app/docker
mvn test > ${LOG}/testLog 2>&1
if [ $? -ne 0 ]
then
echo "test errcode:$?"
cat $LOG/testLog
exit 1
fi
echo 'Test OK'
mvn package -Dmaven.test.skip=true > ${LOG}/packageLog 2>&1
if [ $? -ne 0 ]
then
echo "package errcode:$?"
cat $LOG/packageLog
exit 2
fi
echo 'Package OK '


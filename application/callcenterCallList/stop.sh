#!/bin/bash

JAR_NAME=callcenterCallList.jar

PID=`ps -ef | grep "${JAR_NAME}" | grep -v "grep" | awk '{print $2}'`

if [ ! -n "${PID}" ];
then
    echo "not found ${JAR_NAME} process"
else
    echo "${JAR_NAME} proce's pid: ${PID}"
    kill -s 9 ${PID}
    if [ $? -eq 0 ];
    then
        echo "${JAR_NAME} process killed success";
    else
        echo "${JAR_NAME} process killed failure";
    fi
fi

#!/bin/bash

PROFILE=dev
JAR_NAME=callcenterEureka.jar

nohup java -jar ${JAR_NAME} -Duser.timezone=GMT+8 --spring.profiles.active=${PROFILE} >/dev/null 2>&1 &

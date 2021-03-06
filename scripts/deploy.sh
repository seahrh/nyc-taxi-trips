#!/usr/bin/env bash
set -x #echo on

HOST_PORT=8080
CONTAINER_PORT=9000
IMAGE="nyc-taxi-trips:1.0-SNAPSHOT"
APPLICATION_SECRET="O@MkKLPT2o2=><ddz08f>em5pRa4n>t[FbdQP?o;0XmU^yGGB<0hO^VhShg/vRz_"
PROJECT_DIR=$(cd . ; pwd)
GOOGLE_APPLICATION_CREDENTIALS="nyc-taxi-trips-fd59948fb44d.json"

sbt clean test dist docker:publishLocal

docker run -p ${HOST_PORT}:${CONTAINER_PORT} \
-v ${PROJECT_DIR}/conf:/mnt \
-e APPLICATION_SECRET=${APPLICATION_SECRET} \
-e GOOGLE_APPLICATION_CREDENTIALS=/mnt/${GOOGLE_APPLICATION_CREDENTIALS} \
${IMAGE}

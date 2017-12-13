#!/usr/bin/env sh

export JOB_SCHEDULER_DB_PASSWORD=test

cd $(dirname "$0")/..
./gradlew clean installDist && docker-compose up

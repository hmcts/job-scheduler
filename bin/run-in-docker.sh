#!/usr/bin/env sh

export JOB_SCHEDULER_DB_PASSWORD=test

../gradlew clean installDist && docker-compose up

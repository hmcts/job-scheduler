#!/usr/bin/env bash

set -e

# Database where jobs are persisted
if [ -z "$JOB_SCHEDULER_DB_PASSWORD" ]; then
  echo "ERROR: Missing environment variables. Set value for 'JOB_SCHEDULER_DB_PASSWORD'."
  exit 1
fi

psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=jobscheduler --set PASSWORD=${JOB_SCHEDULER_DB_PASSWORD} <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD ':PASSWORD';

  CREATE DATABASE jobscheduler
    WITH OWNER = :USERNAME
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;
EOSQL

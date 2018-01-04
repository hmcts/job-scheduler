FROM postgres:9.6

ADD init-db.sh /docker-entrypoint-initdb.d

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD psql -c 'select 1' -d jobscheduler -U jobscheduler

EXPOSE 5432

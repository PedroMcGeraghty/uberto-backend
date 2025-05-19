set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE uberto_sql;
    CREATE USER docker WITH ENCRYPTED PASSWORD 'docker';
    GRANT ALL PRIVILEGES ON DATABASE uberto_sql TO docker;
EOSQL
#!/bin/bash
# Run L2Journey SQL scripts: login tables first, then game tables.
# Expects sql files under ./sql (mount dist/db_installer/sql here as sql/).
set -e
BASE="/docker-entrypoint-initdb.d/sql"
if [ ! -d "$BASE" ]; then
  echo "No sql directory found at $BASE, skipping L2Journey init."
  exit 0
fi
for dir in login game updates; do
  if [ -d "$BASE/$dir" ]; then
    for f in $(find "$BASE/$dir" -maxdepth 1 -name "*.sql" | sort); do
      echo "Running $f"
      mysql -u root -p"$MYSQL_ROOT_PASSWORD" "$MYSQL_DATABASE" < "$f"
    done
  fi
done

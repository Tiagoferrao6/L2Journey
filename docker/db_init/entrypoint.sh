#!/bin/sh
set -e
DB_HOST="${DB_HOST:-db}"
DB_PORT="${DB_PORT:-3306}"

mysql_exec() {
  mysql -h "$DB_HOST" -P "$DB_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -N -s "$MYSQL_DATABASE" -e "$1"
}

table_exists() {
  t="$1"
  [ "$(mysql -h "$DB_HOST" -P "$DB_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -N -s -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${MYSQL_DATABASE}' AND table_name='${t}';")" = "1" ]
}

column_exists() {
  t="$1"
  c="$2"
  [ "$(mysql -h "$DB_HOST" -P "$DB_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" -N -s -e "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema='${MYSQL_DATABASE}' AND table_name='${t}' AND column_name='${c}';")" = "1" ]
}

echo "Waiting for MariaDB TCP ${DB_HOST}:${DB_PORT}..."
for i in $(seq 1 30); do
  if mysqladmin ping --protocol=tcp -h "$DB_HOST" -P "$DB_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" --silent >/dev/null 2>&1; then
    echo "MariaDB is ready."
    break
  fi
  echo "MariaDB not ready yet ($i/30), retrying..."
  sleep 1
done

if table_exists "account_data" || table_exists "gameservers"; then
  echo "Login schema already present, skipping Database_Installer_LS."
else
  echo "Running Database_Installer_LS (login)..."
  java -cp "/opt/l2j/libs/*:/opt/l2j/db_installer/Database_Installer_LS.jar" \
    com.l2journey.tools.dbinstaller.LauncherLS \
    -h "$DB_HOST" -p "$DB_PORT" -u root -pw "$MYSQL_ROOT_PASSWORD" \
    -d "$MYSQL_DATABASE" -m i -dir /opt/l2j/sql/login/
fi

if table_exists "items" || table_exists "characters"; then
  echo "Game schema already present, skipping Database_Installer_GS."
else
  echo "Running Database_Installer_GS (game)..."
  java -cp "/opt/l2j/libs/*:/opt/l2j/db_installer/Database_Installer_GS.jar" \
    com.l2journey.tools.dbinstaller.LauncherGS \
    -h "$DB_HOST" -p "$DB_PORT" -u root -pw "$MYSQL_ROOT_PASSWORD" \
    -d "$MYSQL_DATABASE" -m i -dir /opt/l2j/sql/game/
fi

if [ -d /opt/l2j/sql/updates ] && [ -n "$(find /opt/l2j/sql/updates -maxdepth 1 -name '*.sql' 2>/dev/null)" ]; then
  echo "Running updates SQL..."
  for f in $(find /opt/l2j/sql/updates -maxdepth 1 -name '*.sql' | sort); do
    base="$(basename "$f")"
    if [ "$base" = "Update_character_shortcuts.sql" ] && column_exists "character_shortcuts" "character_type"; then
      echo "Skipping $f (character_shortcuts.character_type already exists)"
      continue
    fi
    if [ "$base" = "update_items_dressme.sql" ] && column_exists "items" "visual_item_id"; then
      echo "Skipping $f (items.visual_item_id already exists)"
      continue
    fi
    if [ "$base" = "update_items_agathion_energy.sql" ] && column_exists "items" "agathion_energy"; then
      echo "Skipping $f (items.agathion_energy already exists)"
      continue
    fi
    echo "Applying $f"
    mysql -h "$DB_HOST" -P "$DB_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" "$MYSQL_DATABASE" < "$f"
  done
fi

echo "Database installation complete."

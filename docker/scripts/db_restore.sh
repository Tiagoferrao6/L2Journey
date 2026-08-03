#!/usr/bin/env bash
set -e

# L2Journey Database Restore Script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
BACKUP_DIR="${PROJECT_ROOT}/docker/backups"

TARGET_FILE="$1"
if [ -z "${TARGET_FILE}" ]; then
    TARGET_FILE="${BACKUP_DIR}/latest_backup.sql"
fi

if [ ! -f "${TARGET_FILE}" ]; then
    echo "❌ Error: Backup file not found at ${TARGET_FILE}"
    echo "Usage: ./docker/scripts/db_restore.sh [path/to/backup.sql]"
    exit 1
fi

CONTAINER_NAME=$(podman ps --format "{{.Names}}" | grep -E "l2journey_db|db" | head -n 1)

if [ -z "${CONTAINER_NAME}" ]; then
    echo "❌ Error: MariaDB container is not running!"
    echo "Please ensure the container stack is running (podman-compose up -d)."
    exit 1
fi

DB_NAME="${MYSQL_DATABASE:-l2journey}"

echo "🔄 Restoring L2Journey Database..."
echo "Container: ${CONTAINER_NAME}"
echo "Source:    ${TARGET_FILE}"

podman exec -i "${CONTAINER_NAME}" mariadb \
    -u root -p"${MYSQL_ROOT_PASSWORD:-l2j}" \
    "${DB_NAME}" < "${TARGET_FILE}"

echo "✅ Database restored successfully from ${TARGET_FILE}!"

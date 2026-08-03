#!/usr/bin/env bash
set -e

# L2Journey Database Backup Script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
BACKUP_DIR="${PROJECT_ROOT}/docker/backups"

mkdir -p "${BACKUP_DIR}"

CONTAINER_NAME=$(podman ps --format "{{.Names}}" | grep -E "l2journey_db|db" | head -n 1)

if [ -z "${CONTAINER_NAME}" ]; then
    echo "❌ Error: MariaDB container is not running!"
    echo "Please ensure the container stack is running (podman-compose up -d)."
    exit 1
fi

TIMESTAMP=$(date +"%Y-%m-%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/l2journey_backup_${TIMESTAMP}.sql"
LATEST_FILE="${BACKUP_DIR}/latest_backup.sql"

DB_USER="${MYSQL_USER:-l2j}"
DB_PASS="${MYSQL_PASSWORD:-l2j}"
DB_NAME="${MYSQL_DATABASE:-l2journey}"

echo "📦 Creating L2Journey Database Backup..."
echo "Container: ${CONTAINER_NAME}"
echo "Database:  ${DB_NAME}"

podman exec -i "${CONTAINER_NAME}" mariadb-dump \
    -u root -p"${MYSQL_ROOT_PASSWORD:-l2j}" \
    --add-drop-table \
    --single-transaction \
    "${DB_NAME}" > "${BACKUP_FILE}"

cp "${BACKUP_FILE}" "${LATEST_FILE}"

FILE_SIZE=$(du -h "${BACKUP_FILE}" | cut -f1)

echo "✅ Backup completed successfully!"
echo "📄 File: ${BACKUP_FILE} (${FILE_SIZE})"
echo "🔗 Latest Link: ${LATEST_FILE}"

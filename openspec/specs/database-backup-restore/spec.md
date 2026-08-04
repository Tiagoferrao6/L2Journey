# Database Backup & Restore Specification

## Requirements

### Requirement: Automação de Backup do Banco MariaDB
O sistema MUST fornecer um script de linha de comando idempotente (`./docker/scripts/db_backup.sh`) para gerar backups completos do banco de dados MariaDB com timestamping e rotação de cópia mais recente (`latest_backup.sql`).

#### Scenario: Execução manual do backup
- **WHEN** o administrador executa o script `./docker/scripts/db_backup.sh`
- **THEN** o sistema executa o dump via `mariadb-dump` e salva o arquivo compactado em `./docker/backups/backup_YYYYMMDD_HHMMSS.sql` e copia para `latest_backup.sql`.

### Requirement: Restauração Automatizada via Script
O sistema MUST fornecer um script idempotente (`./docker/scripts/db_restore.sh`) para restaurar a base de dados a partir do último backup válido.

#### Scenario: Restauração de emergência após reset do container
- **GIVEN** o banco de dados foi resetado ou destruído
- **WHEN** o administrador executa `./docker/scripts/db_restore.sh`
- **THEN** o script lê o arquivo `./docker/backups/latest_backup.sql` e o importa para o container `l2journey_db_1`.

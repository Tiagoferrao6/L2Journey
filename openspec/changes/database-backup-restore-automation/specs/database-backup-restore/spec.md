# Spec: Database Backup & Restore Engine

## ADDED Requirements

### Requirement: Automação de Backup de Dados de Personagens
The system MUST provide a CLI script (`docker/scripts/db_backup.sh`) capable of dumping essential player and account tables into timestamped SQL files within `./docker/backups/`.

#### Scenario: Execução do script de backup com servidor ativo
- **GIVEN** os containers MariaDB e GameServer estão ativos
- **WHEN** o administrador executa `./docker/scripts/db_backup.sh`
- **THEN** um arquivo de dump SQL com timestamp é criado em `./docker/backups/` e o ponteiro `latest_backup.sql` é atualizado.

### Requirement: Restauração de Snapshot de Personagens após Reset
The system MUST provide a CLI script (`docker/scripts/db_restore.sh`) capable of importing SQL dumps back into the MariaDB container after container prunes or resets.

#### Scenario: Restauração após podman system reset
- **GIVEN** o ambiente Podman passou por um reset e os containers novos foram inicializados
- **WHEN** o administrador executa `./docker/scripts/db_restore.sh`
- **THEN** os registros de contas e personagens do arquivo `latest_backup.sql` são restaurados na base de dados `l2journey`.

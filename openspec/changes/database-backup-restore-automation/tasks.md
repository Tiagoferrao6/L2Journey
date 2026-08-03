# Tasks: Scripts Automatizados de Backup e Restauração do MariaDB

- [x] Create `./docker/backups/` directory with `.gitignore` for backup artifact management <!-- id: 0 -->
- [x] Implement `./docker/scripts/db_backup.sh` with `mariadb-dump` and timestamping <!-- id: 1 -->
- [x] Implement `./docker/scripts/db_restore.sh` for easy one-command restoration <!-- id: 2 -->
- [x] Add executable permissions (`chmod +x`) to backup/restore scripts <!-- id: 3 -->
- [x] Validate backup and restore cycle after a simulated `podman system reset` <!-- id: 4 -->

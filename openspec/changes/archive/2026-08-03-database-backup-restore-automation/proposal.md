# Proposal: Scripts Automatizados de Backup e Restauração do MariaDB

## Summary
Implementar utilitários CLI executáveis (`docker/scripts/db_backup.sh` e `docker/scripts/db_restore.sh`) e um mecanismo no `db_init` para realizar backup e restauração automatizada de contas (`accounts`), personagens (`characters`), inventário (`items`), habilidades (`character_skills`) e tabelas de IA/Memórias do L2Journey em arquivos `.sql` armazenados na pasta `./docker/backups/`.

## Motivation
Durante o desenvolvimento do L2Journey com Podman, comandos como `podman system reset -f` ou destruição manual de volumes descartam os dados do banco MariaDB. Ter scripts dedicados de backup e restauração rápida evita a perda de progresso dos personagens de teste e permite recuperar snapshot dos dados com um único comando.

## Proposed Changes
- **Directory Structure (`docker/backups/`)**: Adicionar a pasta `./docker/backups/` no repositório com `.gitignore` para versionar apenas o script e manter dumps fora do controle de versão git.
- **Backup Script (`docker/scripts/db_backup.sh`)**: Executa `mariadb-dump` via Podman container extraindo tabelas essenciais de dados de jogadores e grava com carimbo de data/hora (ex: `backup_2026-08-03_0850.sql`) e atualiza o link simbólico `latest_backup.sql`.
- **Restore Script (`docker/scripts/db_restore.sh`)**: Restaura o arquivo `.sql` mais recente ou um arquivo específico especificado por argumento no banco de dados MariaDB ativo.

## Verification
- Testar execução do `db_backup.sh` com o banco em execução e confirmar criação do dump SQL na pasta `./docker/backups/`.
- Testar `podman system reset -f`, reiniciar o ambiente e rodar `db_restore.sh` para confirmar que contas e personagens foram restaurados com sucesso.

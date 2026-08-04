# Design: Scripts Automatizados de Backup e Restauração do MariaDB

## Architecture & Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            EXECUÇÃO DE BACKUP                               │
├─────────────────────────────────────────────────────────────────────────────┤
│  1. Usuário executa: ./docker/scripts/db_backup.sh                         │
│  2. Script detecta o container `l2journey_db_1` via `podman ps`             │
│  3. Executa mariadb-dump para as tabelas:                                   │
│     - accounts, characters, items, character_skills, character_subclasses   │
│     - character_llm_relationships, character_llm_memories                 │
│  4. Grava em: ./docker/backups/players_backup_YYYYMMDD_HHMMSS.sql           │
│  5. Atualiza o atalho: ./docker/backups/latest_backup.sql                   │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                          EXECUÇÃO DE RESTAURAÇÃO                            │
├─────────────────────────────────────────────────────────────────────────────┤
│  1. Usuário executa: ./docker/scripts/db_restore.sh [backup_file.sql]      │
│  2. Script verifica a saúde do container MariaDB                            │
│  3. Importa o SQL via: podman exec -i l2journey_db_1 mariadb ...            │
│  4. Notifica o sucesso da restauração dos personagens e contas.             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Backup Tables Scope
- `accounts`
- `characters`
- `items`
- `character_skills`
- `character_subclasses`
- `character_shortcuts`
- `character_hennas`
- `character_macroses`
- `character_quests`
- `character_llm_relationships`
- `character_llm_memories`

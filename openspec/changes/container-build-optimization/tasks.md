# Tasks: Container Build Optimization & Auto-Pruning

## 1. Otimização de Comandos Shell (`~/.bashrc`)

- [x] 1.1 Atualizar `alias l2rebuild` para focar no serviço `gameserver` e encadear `podman image prune -f`.
- [x] 1.2 Adicionar `alias l2rebuild-all` para reconstrução completa do `gameserver` e `loginserver` com auto-prune.
- [x] 1.3 Adicionar `alias l2clean` para limpeza geral de disco (`podman system prune -f && podman image prune -f`).

## 2. Validação

- [x] 2.1 Testar a execução do `l2clean` e dos aliases no ambiente do usuário.

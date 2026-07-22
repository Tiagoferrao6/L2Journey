# Requisitos e Tuning de Ambiente (Single Player)

## Dependências de Software
| Componente | Versão/Requisito | Descrição |
|---|---|---|
| Java | JDK 21+ | Obrigatório para o compilador e runtime do servidor (L2Journey). |
| MariaDB | 11.4+ | Sistema de banco de dados recomendado para estabilidade local. |
| WSL 2 | Ubuntu 22.04 | (Se usando Windows) Recomendado para deploy do servidor de Login/Game. |

## Requisitos de Hardware (Solo Play)
- **CPU**: 4 Cores / 8 Threads modernos.
- **RAM**: 8GB (idealmente 16GB, reservando 4GB para o Game Server e 2GB para o DB).
- **Disco**: SSD NVMe (Reduz drasticamente o tempo de leitura de geodata).

## Configurações de Tuning (Tunning)
Como o servidor não terá milhares de players simultâneos enviando pacotes, o foco do *tuning* deve ser **simulação de IAs em background**.

### Sugestões de Tunning (Game Server)
- `MaxPlayers` = 50 (O jogador + espaço para IAs visíveis).
- `Geodata` = Ativado, obrigatório para a IA dos Hunters (Pathfinding).
- `ZoneListeners` = Usar bounding boxes amplos (ex: Região inteira de Gludio) para não sobrecarregar a verificação de OnEnterZone/OnExitZone.
- `AI_Update_Rate` = Pode ser aumentado (ex: 2000ms para traders, 500ms para hunters em combate) para economizar ciclos de CPU.

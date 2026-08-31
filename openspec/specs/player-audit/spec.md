# Player Audit Spec

## Visão Geral
O sistema de Player Audit permite que administradores interceptem, filtrem e registrem toda a atividade de rede (Client IN / Server OUT) de jogadores específicos em tempo real, sem prejudicar a performance global do servidor.

## Requisitos (Capabilities)

### 1. Escuta Ativa Direcionada
- O sistema DEVE interceptar pacotes diretamente na camada de rede (leitura/escrita).
- O sistema NÃO DEVE alocar memória adicional (como strings JSON) para pacotes de jogadores que não estão na lista de auditoria.

### 2. Filtro e Configuração em Tempo Real
- O sistema DEVE ler a configuração de `config/player_audit.ini`.
- O sistema DEVE permitir ligar/desligar a auditoria globalmente (`AuditEnabled`).
- O sistema DEVE permitir auditar uma lista específica de jogadores (`AuditMode = LIST`, `AuditPlayerList`).
- O sistema DEVE suportar a recarga (reload) das configurações sem reiniciar o GameServer (via comando admin, ex: `//reload audit`).

### 3. Serialização e Armazenamento
- Pacotes monitorados DEVEM implementar uma interface comum `IAuditablePacket` com o método `Map<String, Object> getAuditData()`.
- O payload capturado DEVE ser convertido para formato JSON.
- A gravação em disco DEVE ser feita de forma assíncrona usando uma fila na memória (ex: `ConcurrentLinkedQueue`) e uma Thread em background.
- O formato do arquivo de saída DEVE ser JSON Lines (`.jsonl`), onde cada linha representa um pacote individual capturado, armazenado em `log/player_actions/<data>/<PlayerName>.jsonl`.

### 4. Controle de Tráfego (Ignorar Movimentos)
- O sistema DEVE ter uma configuração `IgnoreMovementPackets` para descartar pacotes de pura movimentação (`MoveBackwardToLocation`, `ValidatePosition`, etc), a fim de economizar espaço de armazenamento.

## Pacotes Críticos a Serem Monitorados Inicialmente
A implementação inicial focará nas seguintes categorias:
- **Combate/Skills:** `Attack`, `MagicSkillUse`, `Action`, `RequestMagicSkillUse`
- **Ambiente:** `NpcInfo`, `CharInfo`, `SystemMessage`
- **Interação:** `RequestBypassToServer`, `RequestUseItem`
- **Economia/Estado:** `ItemList`, `StatusUpdate`, `UserInfo`

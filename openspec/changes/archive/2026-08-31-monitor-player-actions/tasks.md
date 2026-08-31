# Tasks: Monitor Player Actions

## Fase 1: Fundação do Logger Assíncrono e Configurações
- [x] Criar o arquivo de configuração `dist/game/config/player_audit.ini` (AuditEnabled, AuditMode, AuditPlayerList, etc).
- [x] Atualizar a classe `Config` (`java/com/l2journey/gameserver/Config.java`) para ler e armazenar em memória as variáveis do `player_audit.ini` (incluindo o parser de vírgulas para o HashSet de jogadores).
- [x] Criar a interface `IAuditablePacket` com o método `Map<String, Object> getAuditData()`.
- [x] Criar o `PlayerActionLogger` (Singleton/Thread):
    - [x] Instanciar a fila `ConcurrentLinkedQueue<String>` (ou similar).
    - [x] Implementar a *Worker Thread* que drena a fila e escreve os dados (usando `BufferedWriter` ou `FileWriter`) nos arquivos em `log/player_actions/<data>/<Player>.jsonl`.
    - [x] Implementar a serialização JSON simplificada no `PlayerActionLogger` para transformar o `Map` retornado pelos pacotes em uma string JSONL válida, adicionando o `timestamp`, `direction`, `packetName` e `charName`.

## Fase 2: Interceptação no L2GameClient
- [x] Modificar `L2GameClient.java` no método de recebimento de pacotes (ex: onde decodifica ou onde chama o `read()` do `ClientPacket`):
    - Injetar validação rápida: Se a auditoria estiver ligada E o jogador atual estiver na lista, se o pacote for `IAuditablePacket`, chamar a formatação e enfileirar.
- [x] Modificar `L2GameClient.java` (ou a classe base de `ServerPacket`) no método de envio (`writeImpl` ou no buffer send):
    - Mesma lógica de verificação para enviar pacotes de saída para o logger.

## Fase 3: Instrumentação de Pacotes Essenciais (IN - Client -> Server)
Implementar `IAuditablePacket` nos seguintes pacotes:
- [x] `Action`
- [x] `MoveBackwardToLocation` (Respeitar o filtro de `IgnoreMovementPackets`)
- [x] `RequestMagicSkillUse`
- [x] `RequestBypassToServer`
- [x] `RequestUseItem`
- [x] `Say2` (Chat)

## Fase 4: Instrumentação de Pacotes Essenciais (OUT - Server -> Client)
Implementar `IAuditablePacket` nos seguintes pacotes:
- [x] `NpcInfo` e `CharInfo`
- [x] `Attack`
- [x] `MagicSkillUse`
- [x] `SystemMessage`
- [x] `StatusUpdate` e `UserInfo`

## Fase 5: Integração e Testes
- [x] Adicionar o comando de Admin (handler) para dar reload nas configurações do logger (`//reload audit`) sem reiniciar o server.
- [x] Iniciar o servidor com 2 bots de teste configurados no `.ini` e validar a integridade dos arquivos `jsonl` criados.

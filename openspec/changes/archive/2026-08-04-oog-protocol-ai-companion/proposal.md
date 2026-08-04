# Proposal: Out-Of-Game (OOG) Protocol Driver & Real Account Dual Control

## Why

Atualmente, os companions (como o `PaladinBot`) operam como instâncias efêmeras geradas via scripts com injeções diretas em memória. Para que a experiência de IA seja indistinguível de um jogador humano real, os personagens devem residir em contas reais do banco de dados MySQL (`characters`, `account_data`), operar estritamente através do protocolo de rede do servidor (sem alterações diretas em SQL), suportar criação autônoma de personagens e permitir a transição limpa (Handover Dual Control) entre a execução por IA e o login por um jogador humano através do cliente oficial.

## What Changes

- **OOG Protocol Driver**: Desenvolver um adaptador de protocolo Out-Of-Game (`OOGClientSession`) que emula os pacotes cliente-servidor (`RequestAuthLogin`, `RequestCharacterCreate`, `CharacterSelect`, `MoveToLocation`, `Action`, `RequestBypassToServer`, `RequestBuyItem`, `UseItem`).
- **Character Creation Agent**: Permitir que a IA detecte contas sem personagens e execute o pacote `CharacterCreate` autônomo selecionando raça, classe base (`Human Fighter`/`Mystic`), sexo e aparência.
- **Dual Control & Human Handover**: Caso o jogador humano conecte no cliente oficial L2.exe com a conta do bot, a IA desconecta a sessão OOG graciosamente. Quando o humano desloga, a IA reconecta a sessão OOG e retoma a automação.
- **Persistent Character Sync**: Todo o progresso de itens, adena, nível e quests é salvo nativamente pelo `GameServer` nas tabelas `characters` do MySQL.
- **Multi-Account Raid Squads**: Capacidade de logar 1 a 9 contas OOG sob demanda para a formação de parties táticas e enfrentamento de Raid Bosses.

## Capabilities

### New Capabilities

- `oog-client-session`: Motor de conexão OOG para autenticação, seleção e controle de personagens sem cliente L2 aberto.
- `autonomous-character-creation`: Agente de criação autônoma de personagens selecionando atributos visuais e classe inicial via protocolo.
- `dual-control-handover`: Detecção e gerência de concorrência entre sessão de IA e cliente L2 humano.

### Modified Capabilities

- `coop-companion`: Atualizado para utilizar o driver OOG em substituição à instanciação sintética de `FakePlayer`.

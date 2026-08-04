# autonomous-game-master Specification

## Purpose
TBD - created by archiving change llm-autonomous-game-master. Update Purpose after archive.
## Requirements
### Requirement: Geração Narrativa de Eventos Globais
The system MUST periodically generate unique narrative lore texts and trigger in-game announcements via the Gemini API Storyteller prompt.

#### Scenario: Início de Invasão de Orcs em Gludio
- **GIVEN** o servidor possui mais de 1 jogador ativo na zona de Gludio
- **WHEN** o GM Autônomo agenda um evento de invasão
- **THEN** o sistema dispara um anúncio dramático em tela cheia e na janela de chat narrando o ataque dos Orcs.

### Requirement: Controle Seguro de Spawn e Recompensas
The system MUST execute entity spawns through internal Game Master APIs and limit total reward items to prevent economy inflation.

#### Scenario: Encerramento de Evento com Spawn Clean-up
- **GIVEN** o chefe do evento foi derrotado pelos jogadores
- **WHEN** a condição de vitória é detectada
- **THEN** o GM Autônomo limpa os spawns residuais e faz o anúncio global de encerramento.


# rpg-socialization Specification

## Purpose
TBD - created by archiving change llm-agent-rpg-socialization. Update Purpose after archive.
## Requirements
### Requirement: Resposta a Mensagens Privadas (Whisper/PM)
The system MUST reply to private messages sent to a persistent LLM bot within 3 seconds using the bot's configured Persona and relationship memory.

#### Scenario: Jogador pede ajuda de localização
- **GIVEN** o jogador envia PM "/whisper Bot_SirLancelot Onde fica Abandoned Camp?"
- **WHEN** o servidor processa a mensagem através da Gemini API
- **THEN** o bot responde via PM com a localização exata em coordenadas de Gludio/Gludin.

### Requirement: Reação Tática de Chat em Combate e PvP
The system MUST occasionally broadcast short in-character chat messages during boss fights or after winning PvP duels.

#### Scenario: Trash talk pós vitória PvP
- **GIVEN** o bot derrota um oponente em duelo de arena
- **WHEN** a partida é finalizada
- **THEN** o bot envia mensagem de chat local no tom configurado em sua Persona.


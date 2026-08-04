# bot-memory-relationship Specification

## Purpose
TBD - created by archiving change llm-agent-memory-relationships. Update Purpose after archive.
## Requirements
### Requirement: Persistência de Afetividade e Relacionamento
The system MUST track and persist relationship scores (-100 to +100) between each persistent LLM bot and other characters in the database.

#### Scenario: Jogador ajuda o bot em combate
- **GIVEN** o bot está lutando contra um mob com HP < 30%
- **WHEN** um jogador humano cura o bot ou mata o mob que o atacava
- **THEN** o sistema adiciona +15 pontos de afinidade com o jogador e grava a memória "Curou-me quando eu estava morrendo".

#### Scenario: Jogador comete Kill Steal (KS)
- **GIVEN** o bot está atacando um mob
- **WHEN** um jogador não pertencente à party atinge e mata o mob do bot
- **THEN** o sistema subtrai -20 pontos de afinidade e grava a memória "Roubou meu mob em Abandoned Camp".

### Requirement: Contexto Injetado no Prompt da LLM
The system MUST retrieve and format the top relevant memories for any character in interaction range when generating high-level LLM prompts.

#### Scenario: Injeção de histórico no prompt
- **GIVEN** o bot está em diálogo com o jogador Tiago
- **WHEN** o sistema constrói o prompt para a Gemini API
- **THEN** o sistema inclui as 3 memórias mais recentes relacionadas ao jogador Tiago.


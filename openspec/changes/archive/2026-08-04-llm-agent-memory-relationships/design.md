# Design: Sistema de Memória & Relacionamento (Rivalidades e Amizades)

## Technical Architecture

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                      Player Interaction Listener                            │
 │  (Detects: Player Healed Bot, KS Event, Chat Message, PK Attack, Trade)     │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                        Memory & Relationship Service                        │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • Aggregates event & calculates affinity score delta (-20, +15, etc.)      │
 │  • Summarizes memory context via LLM                                        │
 │  • Persists to MySQL `character_llm_relationships`                          │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         Gemini/Ollama Prompt Injector                       │
 │  Injects: "Top 3 relevant memories with Player X" into LLM context window   │
 └─────────────────────────────────────────────────────────────────────────────┘
```

## Data Schema

### `character_llm_relationships`
- `bot_object_id` (INT) - ID do bot
- `target_object_id` (INT) - ID do jogador/bot alvo
- `target_name` (VARCHAR) - Nome do personagem
- `affinity_score` (INT) - Valor de -100 (Inimigo Declarado) a +100 (Melhor Amigo)
- `relationship_status` (ENUM: ALLY, FRIEND, NEUTRAL, SUSPICIOUS, ENEMY, RIVAL)
- `last_interaction_time` (TIMESTAMP)

### `character_llm_memories`
- `memory_id` (BIGINT AUTO_INCREMENT)
- `bot_object_id` (INT)
- `target_object_id` (INT)
- `event_type` (ENUM: HELPED_IN_COMBAT, STOLE_LOOT, KS_MOB, PK_ATTACK, CHAT_COMPLIMENT, CHAT_INSULT)
- `description` (TEXT) - Resumo curto da memória
- `created_at` (TIMESTAMP)

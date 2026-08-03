# Design: PoC do Agente Companheiro de IA Autônomo (Co-op Companion)

## Architecture Diagram

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         HUMAN PLAYER ("Tiago")                              │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │ (Login/Logout Events, PM Orders, Party)
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                      COMPANION MANAGER & STATE MACHINE                      │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • Controls Modes: ACTIVE_COOP ──► ASSIGNED_MISSION ──► AUTONOMOUS_SOLO     │
 │  • Submits perception states to LLM Provider (Ollama / Gemini API)          │
 │  • Coordinates Behavior Tree (Tanking, Hate, Auto-HP Potion, Looting)       │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                    ┌───────────────────┴───────────────────┐
                    ▼                                       ▼
 ┌─────────────────────────────────────┐ ┌─────────────────────────────────────┐
 │      LLM Perception & Action API    │ │     MySQL DB Persistence Engine     │
 │ (JSON Action Space & Consultations) │ │ (characters, items, companion_state)│
 └─────────────────────────────────────┘ └─────────────────────────────────────┘
```

## State Machine Definition (`CompanionState`)
- `ACTIVE_COOP`: Triggered when human player `Tiago` is online. Bot follows, tanks, and assists in party.
- `ASSIGNED_MISSION`: Active when human is offline and gave a specific command (e.g. "Farm 100 Varnish").
- `AUTONOMOUS_SOLO`: Fallback when offline with no mission. Bot farms safe zones for level and sells junk to NPC merchants.

# Design: Automação de Quests & Mudança de Classe (Quest Solver Engine)

## Architecture

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         Quest State Extractor                               │
 │   Extracts: Active Quests, Quest Items, Next Target Mob, Quest NPC Loc      │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         LLM Quest Goal Planner                              │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • Analyzes current level & Class Transfer requirement                      │
 │  • Determines next quest step: "Talk to Master Harris", "Kill 10 Skeletons" │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                      Behavior Tree Quest Executor                           │
 │  • Drives pathfinding to Quest NPC / Spawn Zone                             │
 │  • Executes NPC dialog bypasses and quest item collection                   │
 └─────────────────────────────────────────────────────────────────────────────┘
```

## Supported Quests Phase 1 (1st Class Transfer)
- Quest ID 35: Path of the Warrior
- Quest ID 37: Path of the Human Knight
- Quest ID 39: Path of the Rogue
- Quest ID 40: Path of the Elven Scout
- Quest ID 42: Path of the Palus Knight

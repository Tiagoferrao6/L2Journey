# Design: Autonomous LLM-Driven Progression for Tanker Companion

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│               LLM-Driven Autonomous Tanker Progression Architecture         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────────────────┐             ┌──────────────────────────────┐  │
│  │   LLMTankerPlannerEngine │             │      LLMGameDataTools        │  │
│  │   (Cognitive JSON Loop)  │ ──────────► │  (NpcData, DropData, Items)  │  │
│  └────────────┬─────────────┘             └──────────────┬───────────────┘  │
│               │                                          │                  │
│               ▼                                          ▼                  │
│  ┌──────────────────────────┐             ┌──────────────────────────────┐  │
│  │   LLMCompanionManager    │             │      LLMQuestNavigator       │  │
│  │   (State Machine Engine) │ ──────────► │      + ClassChangeManager    │  │
│  └────────────┬─────────────┘             └──────────────┬───────────────┘  │
│               │                                          │                  │
│               ▼                                          ▼                  │
│  ┌──────────────────────────┐             ┌──────────────────────────────┐  │
│  │ TownWaypointMeshManager  │ ──────────► │    BuyListExecutingEngine    │  │
│  │ (Gludio Town Mesh & A*)  │             │    (Shots, Potions, Gear)    │  │
│  └──────────────────────────┘             └──────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Detailed Design Components

### 1. LLM Decision Planner Engine (`LLMTankerPlannerEngine`)
- **State Snapshot Builder**: Constructs a JSON representation of `PaladinBot`'s level, class, HP/MP, Adena, inventory supplies, current quest stage, and position.
- **LLM Prompting**: Sends the snapshot to `LLMClient` (Qwen/Ollama) requesting an action decision: `FARM_ZONE`, `GO_TO_SHOP`, `START_QUEST`, `ADVANCE_QUEST`, or `REST`.
- **Action Dispatcher**: Translates JSON decisions into concrete engine calls (`TownWaypointMeshManager`, `BuyListExecutingEngine`, `LLMQuestNavigator`).

### 2. Game Data Tool Queries (`LLMGameDataTools`)
- **`get_mob_drops(itemId)`**: Queries `NpcData.getInstance()` and `DropGroupHolder` for drop chances and mob spawn locations.
- **`get_npc_location(npcId)`**: Queries `SpawnData.getInstance()` for NPC coordinates and town assignment.
- **`get_recommended_zone(level)`**: Returns optimal hunting grounds for Level 1-20, 20-35, and 35-40.

### 3. Quest & Class Transfer Pipeline (`LLMQuestNavigator` & `LLMClassChangeManager`)
- **Quest Triggering**: At Level 19.5, the planner initiates `Q001_PathToKnight`.
- **Waypoint Navigation**: Uses `TownWaypointMeshManager` to route the bot to Sir Aaron (ID 30031) in Gludio Church.
- **Class Promotion**: Upon quest completion, `LLMClassChangeManager` promotes the bot to Knight (Class ID 9) and equips D-Grade gear.

### 4. Supply Replenishment Cycle
- When Soulshots < 100 or Potions < 10, the planner routes the bot to Gludio Grocery Trader via `TownWaypointMeshManager`, executes purchase via `BuyListExecutingEngine`, and reactivates auto-shots.

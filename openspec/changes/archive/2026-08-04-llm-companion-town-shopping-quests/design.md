# Design: Town Waypoint Mesh, BuyList Engine & Quest Navigator

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Town Shopping & Quest Navigation Architecture            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌───────────────────────────┐           ┌─────────────────────────────┐   │
│   │   LLMCompanionManager     │           │   LLMQuestDialogExecutor    │   │
│   │   (State Machine Engine)  │           │   (Quest Dialog Bypasses)   │   │
│   └─────────────┬─────────────┘           └──────────────┬──────────────┘   │
│                 │                                        │                  │
│                 ▼                                        ▼                  │
│   ┌───────────────────────────┐           ┌─────────────────────────────┐   │
│   │  TownWaypointMeshManager  │           │   BuyListExecutingEngine    │   │
│   │  (Gludio/Giran Town Mesh) │           │   (L2Merchant / TradeList)  │   │
│   └─────────────┬─────────────┘           └──────────────┬──────────────┘   │
│                 │                                        │                  │
│                 └───────────────────┬────────────────────┘                  │
│                                     │                                       │
│                                     ▼                                       │
│                         ┌───────────────────────┐                           │
│                         │ GeoEngine (A* Path)   │                           │
│                         │ + FakePlayer AI       │                           │
│                         └───────────────────────┘                           │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Detailed Design Components

### 1. Town Waypoint Mesh (`TownWaypointMeshManager`)
- **Node Graph**: XML/Java definition of key nodes in towns (GK, Center, Grocery, WeaponShop, Blacksmith, Church, Quest NPCs).
- **A* Navigation Segment**: Bot walks node-to-node using `GeoEngine` pathfinding for micro-movements between waypoints.
- **Stuck Detection**: If a bot remains at the same coordinate for > 5 seconds while attempting a node transition, recalculate path or step back 50 units.

### 2. BuyList Executing Engine (`BuyListExecutingEngine`)
- **Supply Check**: Monitors companion supplies (e.g. D-Grade Soulshots count < 100, Healing Potions < 10).
- **Merchant Interaction**:
  1. Navigate to nearest Grocery Trader or Weaponsmith using `TownWaypointMeshManager`.
  2. Target NPC (`setTarget(merchantNpc)`).
  3. Query `L2TradeList` for target item IDs (e.g. Item ID 1463 for D-Grade Soulshot, 1061 for Healing Potion).
  4. Verify bot Adena balance.
  5. Execute `doBuy(TradeList, ItemId, Count)` and add items to bot inventory.
  6. Auto-equip/auto-activate Soulshots (`bot.addAutoSoulShot(itemId)`).

### 3. Quest Navigator & Planner (`LLMQuestNavigator`)
- **Quest Location Mapping**: Map Quest NPC IDs to town or zone waypoints.
- **State Progression Loop**:
  1. Determine current `QuestState` condition.
  2. Retrieve target NPC ID and location.
  3. Navigate bot to target NPC location via `TownWaypointMeshManager` / `FakeHunterWaypointsParser`.
  4. Invoke `LLMQuestDialogExecutor.talkToQuestNpc(bot, npcId, questName, eventName)`.
  5. Confirm `cond` advancement and output progress log/whisper to human partner.

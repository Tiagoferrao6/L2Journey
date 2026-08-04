# Proposal: Autonomous LLM-Driven Progression for Tanker Companion (Level 1-40 & Class Quests)

## Why
While AI companions currently follow state machine rules for basic combat and static town teleports, validating a true LLM-driven autonomous AI agent requires an LLM Planner Engine that dynamically queries game database templates (`NpcData`, `DropData`, `ItemData`), determines optimal hunting zones, executes 1st (Knight) and 2nd (Paladin) Class Transfer quests, and manages town supply trips from Level 1 to 40 without hardcoded scripts.

## What Changes
- **LLM Decision Planner Engine (`LLMTankerPlannerEngine`)**: A JSON-driven cognitive decision loop connecting `LLMClient` to `LLMCompanionManager` for high-level action selection (Hunting, Town Supplies, Class Quests).
- **Game Data Tool Queries (`LLMGameDataTools`)**: Expose `NpcData`, `DropData`, and `ItemData` to the LLM via tool-calling functions (`get_mob_drops`, `get_npc_location`, `get_recommended_zone`).
- **Autonomous Class Quest Executor**: Connect `LLMQuestNavigator` and `LLMClassChangeManager` to automatically guide `PaladinBot` to quest NPCs, execute dialog bypasses, and apply Class Transfers (Human Fighter -> Knight -> Paladin).
- **Consumable & Gear Supply Loop**: Trigger `BuyListExecutingEngine` when Soulshots or Health Potions are low, allowing the bot to navigate Gludio via `TownWaypointMeshManager` to purchase supplies and auto-activate shots.

## Risks & Mitigations
- **LLM Response Latency**: Run `LLMTankerPlannerEngine` asynchronously with fallback heuristic rules if LLM response exceeds 3 seconds.
- **Quest Dialog Stalls**: Fall back to direct event bypass execution if NPC dialog parsing returns unrecognized responses.
- **Adena Depletion**: Prioritize high-Adena mob zones if inventory Adena falls below purchasing thresholds.

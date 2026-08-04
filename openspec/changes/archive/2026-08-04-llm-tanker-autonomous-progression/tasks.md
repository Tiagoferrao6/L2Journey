# Tasks: Autonomous LLM-Driven Progression for Tanker Companion

- [x] 1. Implement LLM Decision Planner Engine (`LLMTankerPlannerEngine`) <!-- id: 1 -->
  - [x] 1.1 Create `LLMTankerPlannerEngine.java` for building game state snapshots and querying `LLMClient`.
  - [x] 1.2 Implement JSON action response parser (`FARM_ZONE`, `GO_TO_SHOP`, `START_QUEST`, `ADVANCE_QUEST`, `REST`).

- [x] 2. Implement Game Data Tools (`LLMGameDataTools`) <!-- id: 2 -->
  - [x] 2.1 Create `LLMGameDataTools.java` exposing `NpcData`, `DropGroupHolder`, `SpawnData`, and `ItemData` queries.
  - [x] 2.2 Add unit tests for mob drop lookup and NPC coordinate retrieval.

- [x] 3. Integrate Quest & Class Transfer Pipeline <!-- id: 3 -->
  - [x] 3.1 Connect `LLMTankerPlannerEngine` with `LLMQuestNavigator` for automated `Q001_PathToKnight` execution.
  - [x] 3.2 Implement class promotion triggering in `LLMClassChangeManager` upon quest completion.

- [x] 4. System Integration & End-to-End Validation <!-- id: 4 -->
  - [x] 4.1 Update `LLMCompanionManager` to trigger `LLMTankerPlannerEngine` ticks during `AUTONOMOUS_SOLO` mode.
  - [x] 4.2 Validate build compilation and end-to-end level 1-40 loop integration.

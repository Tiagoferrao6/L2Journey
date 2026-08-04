# Tasks: Town Waypoint Mesh, BuyList Engine & Quest Navigator

- [x] 1. Define Town Waypoint Mesh Graph (`TownWaypointMeshManager`) <!-- id: 1 -->
  - [x] 1.1 Create `TownWaypointMeshManager.java` with node graph for Gludio (GK, Town Square, Grocery Trader, Weaponsmith, Church, Blacksmith).
  - [x] 1.2 Implement node-to-node walking logic combining `GeoEngine` A* pathfinding and stuck-detection fallback.

- [x] 2. Implement BuyList Executing Engine (`BuyListExecutingEngine`) <!-- id: 2 -->
  - [x] 2.1 Create `BuyListExecutingEngine.java` for inspecting companion consumable levels (Soulshots, Health Potions).
  - [x] 2.2 Implement NPC interaction, `L2TradeList` item lookup, Adena validation, and `doBuy` transaction execution.
  - [x] 2.3 Add auto-activation of purchased Soulshots into `FakePlayer` inventory.

- [x] 3. Implement Quest Navigator & Dialog Planner (`LLMQuestNavigator`) <!-- id: 3 -->
  - [x] 3.1 Create `LLMQuestNavigator.java` to map Quest NPC locations to town and field waypoints.
  - [x] 3.2 Connect `LLMQuestNavigator` with `LLMQuestDialogExecutor` for automated dialog bypass execution.

- [x] 4. Integration & State Machine Update (`LLMCompanionManager`) <!-- id: 4 -->
  - [x] 4.1 Update `LLMCompanionManager` to trigger shopping routine when consumables are low in town.
  - [x] 4.2 Add PM whisper command `shop` / `comprar` for manual trigger of town shopping.
  - [x] 4.3 Add unit tests and verify build compilation via `ant`.

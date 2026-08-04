# Proposal: Town Navigation Mesh, BuyList Engine & Quest Navigator for AI Companions

## Summary
Enhance the AI Co-op Companion system (`LLMCompanionManager` & `FakePlayer`) with full urban navigation capabilities, autonomous in-game item purchasing (Soulshots, Potions, Consumables), and multi-step Quest execution using waypoint networks and pathfinding.

## Motivation
Currently, AI Companions can follow players, engage in tactical combat, and sell loot via synthetic inventory updates. However, they lack:
1. **Urban Navigation**: Companions often get stuck on walls, staircases, and buildings in dense towns like Gludio or Giran when trying to navigate to vendors or NPCs.
2. **Autonomous Merchant Interactions**: Companions cannot visit town merchants (Grocery, Weaponsmith, Armorsmith) or execute real `BuyList` transactions to replenish supplies (Soulshots, Health Potions, Scrolls of Escape).
3. **End-to-End Quest Navigation**: Companions cannot navigate across town and field waypoints to speak with Quest NPCs, complete quest dialogs, and progress quest conditions autonomously.

## Proposed Changes
- **Town Waypoint Mesh + Dynamic A***: Implement a node-based waypoint mesh for major towns (Gludio, Giran) connecting Gatekeepers, Town Squares, Grocery Traders, Weaponsmiths, and Quest NPCs. Combine node navigation with `GeoEngine` A* for obstacle avoidance.
- **BuyList Executing Engine**: Extend `LLMCompanionManager` / `FakePlayer` to interact with `L2MerchantInstance` / `L2TradeList` to purchase consumables (Soulshots, Potions) using Adena when inventory supplies run low.
- **Quest Navigator & Planner**: Connect `LLMQuestDialogExecutor` to the waypoint navigation mesh, enabling companions to walk to Quest NPCs across zones, execute dialog bypasses, and report progress.

## Risks & Mitigations
- **Pathfinding Stalls**: If a waypoint route is blocked, fall back to `GeoEngine` A* or emergency short-range teleporting if stuck for > 10 seconds.
- **Insufficient Adena**: Verify Adena balances before attempting `BuyList` transactions to prevent transaction failures.
- **NPC Selection Range**: Ensure waypoints place the bot within 150 units of target NPCs before attempting dialog bypasses.

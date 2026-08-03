# Design: Fake Traders Engine

## Architecture
- **FakePlayer Class**: A new class `FakePlayer extends Player`. Since it extends `Player`, it bypasses the limitations of `NpcTemplate` and naturally supports `TradeList`, `PrivateStoreType`, `RecipeBook`, and MP consumption natively.
- **FakeTraderManager**: A central clock (`ThreadPool.scheduleAtFixedRate`) that orchestrates spawn, despawn, and resets of the bots based on their `<renewTime>` (window of activity).
- **XML Data Engine**: Two main XML parsers:
  1. `FakeTradersEconomyParser`: Reads `fake_traders_economy.xml` (the templates for BUY, SELL, CRAFT prices, amounts, and fees).
  2. `FakeTradersSpawnParser`: Reads `fake_traders_spawns.xml` (the actual bots, coordinates, names, appearance, and which economy template they use).

## The 3 Modes
1. **SELL (Private Store - Sell)**:
   - Behavior: Randomly selects 1-3 items from the linked `<marketProfile>`. Adds them to the FakePlayer's inventory and sets them up in `TradeList`.
   - Economy Impact: **Adena Sink**. Players buy the items, the bot receives adena. When the bot despawns/renews, its adena is deleted.
2. **BUY (Private Store - Buy)**:
   - Behavior: Randomly selects 1-3 items to buy at lower prices (spread).
   - Economy Impact: **Adena Source**. To avoid rewriting L2J core trade checks, the bot is injected with `1,000,000,000` Adena upon spawning. It pays players natively.
3. **CRAFT (Private Manufacture)**:
   - Behavior: Randomly selects 1-3 recipes from the `<craftProfile>`. Registers them to the bot's `RecipeBook`.
   - Economy Impact: **Adena Sink**. Players supply materials and pay the crafting fee. Bot receives the fee, which is deleted upon despawn.
   - Trick: To avoid the bot running out of MP while crafting, we automatically restore its MP to 100% when a player requests a craft.

## Renewal Cycle (Activity Window)
Every bot has a `<renewTime>` (e.g. `4h`). The `FakeTraderManager` checks active bots. When `Time.now() > bot.spawnTime + bot.renewTime`:
1. Stand up / close store.
2. Wipe inventory / adena.
3. Randomly select a new subset of 1-3 items from the XML template.
4. Randomize quantities and prices (within the XML min/max ranges).
5. Sit down / open store with updated title.

## Security & Edge Cases
- **DB Ghost Objects**: The `FakePlayer` class MUST override `store()` and `storeCharBase()` to return immediately. This prevents the fake players (and their injected adena) from ever being saved to the database, ensuring zero DB I/O bloat and removing security exploits.
- **Renewal Collision**: The `FakeTraderManager` loop must check `FakePlayer.getClientInteract()` (or similar trade state). If a real player is currently trading with the bot, the reset logic is delayed to prevent client crashes.
- **Automated Name Protection**: The `FakeTradersSpawnParser` will automatically inject all parsed bot names into `CharNameTable`'s restricted names list during server boot. This guarantees real players cannot create characters with bot names, preventing visual bugs and chat/trade conflicts.

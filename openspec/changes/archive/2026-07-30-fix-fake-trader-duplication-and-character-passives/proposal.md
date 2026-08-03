## Why

During testing of Fake Traders and the test character in Town of Gludio:
1. **Duplicate Trader Names**: Multiple Fake Trader bots spawn with identical names (e.g. two `MatHunter` bots in the same city) because spawn parsing does not verify if a character name is already active in the game world.
2. **Duplicate Items in Store**: Private stores (Buy and Sell) display duplicate item entries (e.g. two separate slots for `Animal Bone` in `MatHunter`'s buy list) because the item selection loop randomly selects from economy profiles with replacement without deduplicating `itemId`s.
3. **Incomplete Test Character Seeding**: The test character `KaelTyrant` is missing passive skills (`Fist Weapon Mastery`, `Light Armor Mastery`, `Boost Attack Speed`, `Agile Movement`, `Force Mastery`, `Expertise C`), and `z_seed_test_account.sql` uses `INSERT IGNORE` without deleting existing character records, causing updates to be ignored when `charId` conflicts exist.

## What Changes

- **Deduplicate Store Items**: Update `FakePlayer.setupSellStore` and `FakePlayer.setupBuyStore` to select unique `itemId`s from economy profiles, eliminating duplicate item slots in trade windows.
- **Enforce Unique Trader Names**: Update `FakeTradersSpawnParser` to skip names that are already present in `World` or `FakeTraderManager`, preventing duplicate bot names.
- **Complete Test Character SQL Seed**: Update `z_seed_test_account.sql` with explicit `DELETE` statements for character `268435457` before re-inserting, and include all Level 40 Tyrant passive and active skills.

## Capabilities

### Modified Capabilities
- `fake-traders-engine`: Ensure unique trader names per spawn and unique items per store session.
- `test-account-seed`: Fully seed Orc Tyrant (`KaelTyrant`) with all Level 40 passive and active skills.

## Impact

- `com.l2journey.gameserver.model.actor.instance.FakePlayer`
- `com.l2journey.gameserver.data.xml.impl.FakeTradersSpawnParser`
- `com.l2journey.gameserver.managers.FakeTraderManager`
- `dist/db_installer/sql/updates/z_seed_test_account.sql`

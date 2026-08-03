## Context

During gameplay testing in Town of Gludio, two issues were observed in the Fake Traders system and test character setup:
- Bots were spawned with duplicate names (e.g. two `MatHunter` traders sitting in Gludio).
- Trade windows (Buy & Sell) contained duplicate entries of the same item (e.g. two separate rows for `Animal Bone` in `MatHunter`'s buy list).
- The test character `KaelTyrant` was missing core passive skills (`Fist Weapon Mastery`, `Light Armor Mastery`, `Boost Attack Speed`, etc.), and `z_seed_test_account.sql` did not clean up existing records before re-inserting.

## Goals / Non-Goals

**Goals:**
- Guarantee unique trader names per world instance during fake trader spawning.
- Ensure `FakePlayer.setupSellStore` and `setupBuyStore` pick distinct `itemId`s so no duplicate item slots appear in trade windows.
- Update `z_seed_test_account.sql` to issue `DELETE` statements before inserting character `268435457` and seed all Orc Tyrant Level 40 passive and active skills.

**Non-Goals:**
- Changing the underlying client packet format or core trade handler logic.

## Decisions

1. **Unique Item Selection in Fake Player Stores**:
   - In `FakePlayer.setupSellStore` and `FakePlayer.setupBuyStore`, shuffle a copy of `profile.getItems()` or track selected `itemId`s in a `Set<Integer>`. Skip any item ID that has already been added to the current store session.

2. **World Name Check During Spawn**:
   - In `FakeTradersSpawnParser`, verify `World.getInstance().getPlayer(name) == null` and `!FakeTraderManager.getInstance().isNameTaken(name)` before creating a new `FakePlayer`.

3. **Complete SQL Reset and Skill Seeding**:
   - Add explicit `DELETE FROM` statements in `z_seed_test_account.sql` for `charId = 268435457` (in `characters`, `items`, `character_skills`).
   - Add all Level 40 Tyrant passive skills:
     - `Fist Weapon Mastery` (Skill 210, Level 11)
     - `Light Armor Mastery` (Skill 233, Level 13)
     - `Boost Attack Speed` (Skill 168, Level 1)
     - `Agile Movement` (Skill 319, Level 2)
     - `Force Mastery` (Skill 993, Level 2)
     - `Toughness` (Skill 134, Level 1)
     - `Expertise C` (Skill 239, Level 2)

## Risks / Trade-offs

- **[Risk]**: If a profile has fewer unique items than the requested count (1 to 3), the store might display fewer items.
  - *Mitigation*: All existing profiles in `fake_traders_economy.xml` contain 3 to 6 distinct items, ensuring sufficient variety.

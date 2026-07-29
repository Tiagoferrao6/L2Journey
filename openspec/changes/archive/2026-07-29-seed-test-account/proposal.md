## Why

To allow instant testing and verification of in-game features (such as Fake Player Traders/Hunters, Mercenary Healers, and Buffer NPCs) without needing manual character creation and grinding, we need an automated SQL seed script that injects a pre-configured test account and character (`KaelTester`) directly during container startup (`db_init`).

## What Changes

- Add a new SQL update script `z_seed_test_account.sql` in `dist/db_installer/sql/updates/` that automatically executes during MariaDB initialization.
- Create a pre-configured account (`tester` / `tester`).
- Create a Level 40 Human Gladiator character (`KaelTester`) located in Town of Gludio (`X: -12787, Y: 122779, Z: -3112`), equipped with C-Grade gear (Dual Samurai Longswords, Full Plate Set, C-Grade Jewels).
- Seed 5,000,000 Adena, C-Grade Soulshots, Greater Healing Potions, Scrolls of Escape, and trade materials (Animal Bone, Iron Ore, Coal, Varnish) for testing trader/economy NPCs.

## Capabilities

### New Capabilities
- `test-account-seeding`: Automatically populates a fully equipped Level 40 Gladiator test account during database initialization.

### Modified Capabilities

## Impact

- Database initialization (`db_init` container).
- `dist/db_installer/sql/updates/z_seed_test_account.sql`.

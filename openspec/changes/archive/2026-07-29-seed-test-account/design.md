## Context

To perform end-to-end testing of L2Journey features (including Fake Player Economy, Trader WTB/WTS, Hunter bots, and Mercenary Healers) without registering new accounts or leveling manually, we need an automated SQL seed file.

This design specifies the SQL seed script `z_seed_test_account.sql` placed in `dist/db_installer/sql/updates/`, which is executed by `docker/db_init/entrypoint.sh` when initializing or updating the database.

## Goals / Non-Goals

**Goals:**
- Automatically create test account `tester` (password: `tester`) and character `KaelTester` (Level 40 Human Gladiator) spawned in Town of Gludio (`X: -12787, Y: 122779, Z: -3112`).
- Equip C-Grade gear: Dual Samurai Longsword (`2626`), Full Plate Set (Chest `356`, Helm `2414`, Gloves `2459`, Boots `2439`), C-Grade Jewels (`875`, `847`, `906`).
- Populate consumable items: 5,000,000 Adena (`57`), 10,000 C-Grade Soulshots (`1464`), 500 Greater Healing Potions (`1061`), 20 Scrolls of Escape (`736`).
- Populate trade testing materials: Animal Bone (`1872`: 1,500), Iron Ore (`1869`: 800), Coal (`1870`: 500), Varnish (`1865`: 300).
- Populate basic Level 40 Gladiator skills in `character_skills`.

**Non-Goals:**
- Creating multiple test characters (single Gladiator character is sufficient for MVP).
- Overwriting existing user characters if `KaelTester` already exists (`INSERT IGNORE INTO` syntax).

## Decisions

- **Use SQL Update Script (`z_seed_test_account.sql`)**: Placed in `dist/db_installer/sql/updates/` so it runs automatically via `entrypoint.sh` without requiring extra build steps.
- **`INSERT IGNORE INTO` Pattern**: Ensures idempotency when container restarts.
- **Fixed Object IDs for Test Items**: Prevents primary key collisions by using offset ranges (e.g. `268435457` for `char_id` and `900000001`+ for `object_id`).

## Risks / Trade-offs

- **[Risk] ID Collision**: If `char_id` 268435457 already exists, insertion might be ignored.
  - *Mitigation*: Use `INSERT IGNORE` and reserve high ID range for test seed.

# test-account-seeding Specification

## Purpose
TBD - created by archiving change seed-test-account. Update Purpose after archive.
## Requirements
### Requirement: Database Seeding for Test Account
The system SHALL execute SQL database initialization scripts to insert a pre-configured test account `tester` and character `KaelTester` upon database startup.

#### Scenario: Database Initialization Seeds Test Account
- **WHEN** MariaDB completes initial schema installation or update
- **THEN** Account `tester` and Level 40 Gladiator character `KaelTester` are present in the database in Town of Gludio (`X: -12787, Y: 122779, Z: -3112`) with C-Grade equipment, consumables, Adena, and materials

### Requirement: Test Character Equipment and Inventory Setup
The system SHALL populate the `items` database table with C-Grade Dual Samurai Longswords, Full Plate Armor, basic C-Grade Jewels, Adena, Soulshots, Healing Potions, and trade testing materials.

#### Scenario: Character Inventory Populated
- **WHEN** Player logs into character `KaelTester`
- **THEN** The inventory contains 5,000,000 Adena, 10,000 C-Grade Soulshots, 500 Greater Healing Potions, 20 Scrolls of Escape, Dual Samurai Longswords, Full Plate Set, and crafting materials (Animal Bone, Iron Ore, Coal, Varnish)


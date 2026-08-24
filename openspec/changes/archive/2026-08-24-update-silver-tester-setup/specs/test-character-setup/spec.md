## ADDED Requirements

### Requirement: Test Character Economy and Supplies Population
The test setup process SHALL populate character `SilverTester` (charId `300000000`) with 1,000,000,000 Adena, S-grade consumables, enhancement materials, and boss access items.

#### Scenario: Verify Adena and consumable items in inventory
- **WHEN** the setup script runs for character `SilverTester`
- **THEN** the character's Adena count is set to 1,000,000,000 (`item_id = 57`), and consumables including 500x Greater CP Potions, 500x Greater Healing Potions, 500x Mana Potions, 100x Blessed Scroll of Escape, 100x Blessed Scroll of Resurrection, 100.000x Soulshot S-Grade, and 100.000x Blessed Spiritshot S-Grade are present in inventory.

### Requirement: Tattoo Level 6 Complete Set Provisioning
The test setup process SHALL insert all Level 6 Tattoos (Right slot underwear IDs 41006, 41012, 41018, 41024, 41030, 41036, 41042 and Left slot hair2 IDs 41048, 41054, 41060, 41066, 41072, 41078, 41084) into `SilverTester`'s inventory.

#### Scenario: Verify all Level 6 Tattoos in inventory
- **WHEN** the setup script populates tattoo items
- **THEN** all 14 Level 6 Tattoo item IDs are registered in the `items` table with owner ID `300000000`.

### Requirement: Cumulative Subclass Skills Association
The test setup process SHALL inject `Sword Muse` (Class ID 107) song skills into the Main Class (`Moonlight Sentinel`, Class ID 102) of `SilverTester` and populate cumulative skill sets across all active subclasses (`Duelist`, `DreadNought`, `Archmage`).

#### Scenario: Verify cumulative skills in character skills table
- **WHEN** the cumulative subclass skill injection executes
- **THEN** the `character_skills` table contains maximum level `Sword Muse` skills (such as Song of Hunter, Song of Warding, Song of Wind, Song of Renewal) assigned to `charId = 300000000`.

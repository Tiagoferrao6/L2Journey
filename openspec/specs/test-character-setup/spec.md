# Capabilities: Test Character Setup

## Requirements

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

### Requirement: TitanTester Character Provisioning with Max Skill Enchants and Hero Status
The test setup process SHALL create and populate character `TitanTester` (`charId = 300000001`) under account `tester` with base class `Titan` (Class ID 113), level 85, Noblesse status, active Hero status, and full Clan skills in clan `100000`.

#### Scenario: Verify TitanTester basic attributes, Hero status, and Clan membership
- **WHEN** the setup script runs for character `TitanTester`
- **THEN** `charId = 300000001` is created with level 85, base class `113`, `nobless = 1`, Hero status in `heroes` table, and membership in clan `100000` with max Clan skills.

### Requirement: TitanTester Subclasses and Skill Injection
The test setup process SHALL create 3 active subclasses for `TitanTester`: Warrior (`DreadNought`, ID 89), Summoner (`Spectral Master`, ID 111), and Assassin (`Ghost Hunter`, ID 108), and inject all Titan main class skills at maximum level fully enchanted, along with cumulative subclass skills.

#### Scenario: Verify TitanTester skills and subclass skill accumulation
- **WHEN** skill injection executes for `TitanTester`
- **THEN** `character_skills` contains all Titan skills at max level and max enchantment (+30/+15), as well as cumulative subclass skills for DreadNought, Spectral Master, and Ghost Hunter.

### Requirement: TitanTester Custom Equipment, Tattoos, Jewelry, and Supplies
The test setup process SHALL insert into `TitanTester`'s inventory the complete Royal Armor sets (Heavy, Light, Robe, Cloak, Shield), all 16 custom Royal Dynasty Weapons, all 14 Level 6 Tattoos, epic boss jewels, 1,000,000,000 Adena, S-grade consumables, enhancement codes, and boss access items.

#### Scenario: Verify TitanTester equipment and inventory contents
- **WHEN** the item population script executes for `TitanTester`
- **THEN** all Royal armor pieces (IDs 99200-99224), all 16 Royal Dynasty Weapons (IDs 99300-99315), all 14 Level 6 Tattoos (IDs 41006-41042, 41048-41084), epic boss jewels (Baium, Zaken, Antharas, Valakas, Queen Ant, Beleth), Adena, CP/HP/MP potions, SS/BSS, Life Stones, and Boss Access items are present in inventory under owner ID `300000001`.

### Requirement: Test Character Provisioning and Base Class Model Binding
The test setup process SHALL create and populate `SilverTester` (`charId = 300000000`) and `TitanTester` (`charId = 300000001`) with explicit base class mappings (`class_index = 0`) in `character_subclasses`, matching their race, sex, and base class IDs (`Moonlight Sentinel` 102 for SilverTester, `Titan` 113 for TitanTester), ensuring correct 3D model loading without fallback rendering.

#### Scenario: Verify base class model rendering and attributes
- **WHEN** `SilverTester` or `TitanTester` logs into the server
- **THEN** the character SHALL render as their proper race/class 3D model (Elf Female for SilverTester, Orc Male for TitanTester) at level 85 with max base HP/CP/MP stats

### Requirement: Inventory Weight Limit Calibration
The test setup process SHALL calibrate inventory shot stacks to 5,000 Soulshots S-Grade and 5,000 Blessed Spiritshots S-Grade for both test characters, maintaining total inventory weight strictly below 100% capacity.

#### Scenario: Verify inventory weight status
- **WHEN** checking character inventory weight gauge on login
- **THEN** weight status SHALL display below 100% (No Overweight status icon or movement penalty)

### Requirement: Hero Status and Clan Provisioning
The test setup process SHALL register both `SilverTester` and `TitanTester` in the `heroes` database table with active Hero status, Hero skills, and full Clan leader privileges in `TesterClan` (`clanid = 100000`).

#### Scenario: Verify Hero aura and skills
- **WHEN** `SilverTester` or `TitanTester` is spawned in game
- **THEN** the character SHALL display active Hero aura and possess Hero skills in skill window

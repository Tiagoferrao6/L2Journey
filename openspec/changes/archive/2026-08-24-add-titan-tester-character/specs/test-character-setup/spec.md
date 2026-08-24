## ADDED Requirements

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

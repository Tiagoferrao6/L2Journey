# test-character-setup Specification

## ADDED Requirements

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

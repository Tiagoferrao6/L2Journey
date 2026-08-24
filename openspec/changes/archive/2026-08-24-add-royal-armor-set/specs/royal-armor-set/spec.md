## ADDED Requirements

### Requirement: S Grade Level 76 Custom Items Definition
The game server SHALL define custom S Grade armor items (Royal Breastplates for all Dynasty class specializations, Royal Gaiters, Royal Helmet, Royal Gauntlets, Royal Boots, Royal Shield, Royal Sigil, and **Royal Cloak**) with `crystal_type="S"`, minimum level requirement of 76 (`<player minLevel="76" />`), icon paths pointing to standard Dynasty icons, and base stats exceeding S84 Elegia armor pieces.

#### Scenario: Equipping a Royal Armor Piece at Level 76
- **WHEN** a player of level 76 or higher equips any S Grade Royal Set armor piece
- **THEN** the character receives the corresponding base P.Def, M.Def, or Shield P.Def attributes defined for that piece

#### Scenario: Attempting to equip Royal Armor below Level 76
- **WHEN** a player below level 76 attempts to equip a Royal Set armor piece
- **THEN** the system prevents equipping the item due to level requirement

### Requirement: Royal Cloak (Capa Royal) & Resurrection Skill
The game server SHALL define the Royal Cloak (Capa Royal) equipped in the back slot (`bodypart="back"`), granting advanced passive stats and providing an active skill **Royal Resurrection** that resurrects a fallen target with 70% XP recovery (`power="70"`).

#### Scenario: Using Royal Resurrection Skill from Royal Cloak
- **WHEN** a player uses the Royal Resurrection skill from the Royal Cloak on a dead target
- **THEN** the target player receives a resurrection prompt recovering 70% of lost experience

### Requirement: Royal Set Bonus Skills
The game server SHALL provide custom passive skills for each Royal Set specialization matching Option A (Shield Master, Weapon Master, Force Master, Bard, Dagger Master, Bow Master, Healer, Enchanter, Summoner, Wizard), applying boosted passive stats (STR/DEX/CON/INT/WIT/MEN, P.Atk%, M.Atk%, Critical Rate, Max HP/MP, Speed, and Status Resistances) when the matching set pieces are equipped.

#### Scenario: Equipping full Royal Armor Set
- **WHEN** a player equips all 5 matching parts of a Royal Armor Set
- **THEN** the server applies the corresponding Royal Set bonus skill to the player

#### Scenario: Equipping Royal Shield with Royal Heavy Set
- **WHEN** a player equips the Royal Shield alongside a Royal Heavy Armor Set
- **THEN** the server applies the Royal Shield bonus skill (increased Shield P.Def / Block Rate)

### Requirement: Royal Armor Set Registration
The game server SHALL define armor set entries in `dist/game/data/stats/armorsets/royal_set.xml` mapping chest, legs, head, gloves, feet, shield/sigil IDs to their respective set skills and +6 enchant bonuses.

#### Scenario: Enchanting Royal Armor Set to +6 or higher
- **WHEN** a player equips a full Royal Set with all parts enchanted to +6 or higher
- **THEN** the server applies the S-Grade Heavy/Light/Robe +6 enchant bonus skill to the player

### Requirement: Client Data DAT Integration
The project SHALL provide formatted client DAT file entries (`itemname-e.dat` and `ArmorGrp.dat`) allowing client UI to display "Royal ..." display names, descriptions, cloak models, and icon textures matching the Dynasty visual armor meshes.

#### Scenario: Inspecting item in client inventory
- **WHEN** a player views a Royal Set item or Royal Cloak in their client inventory
- **THEN** the client renders the name "Royal [Piece Name]" with the Dynasty icon and 3D mesh model

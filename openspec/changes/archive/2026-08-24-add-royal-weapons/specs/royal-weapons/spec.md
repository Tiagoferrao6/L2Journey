## ADDED Requirements

### Requirement: Royal Dynasty 16 Weapons Definition (S Grade / Level 76+)
The game server SHALL define 16 custom S Grade weapon items (`Royal Dynasty Blade - Great Gale`, `Royal Dynasty Guardian - Destruction`, `Royal Dynasty Crusher - Great Gale`, `Royal Dynasty Cudgel - Thunder`, `Royal Dynasty Baghnakh - Great Gale`, `Royal Dynasty Knife - Earth`, `Royal Dynasty Bow - Storm`, `Royal Dynasty Crossbow - Gale`, `Royal Dynasty Halberd - Earthquake`, `Royal Dynasty Dual Blade - Great Gale`, `Royal Dynasty Dual Daggers - Shadow`, `Royal Dynasty Phantom - Nature`, `Royal Dynasty Mace - Wisdom`, `Royal Dynasty Staff - Chaos`, `Royal Dynasty Rapier - Breeze`, `Royal Dynasty Ancient Sword - Destruction`) with `crystal_type="S"`, minimum level requirement 76 (`<player minLevel="76" />`), and icon/mesh paths pointing to Dynasty weapon models.

#### Scenario: Equipping a Royal Dynasty Weapon at Level 76
- **WHEN** a player of level 76 or higher equips any S Grade Royal Dynasty weapon
- **THEN** the character receives the base P.Atk, M.Atk, Crit Rate, and Attack Speed attributes for that weapon

#### Scenario: Attempting to equip Royal Dynasty Weapon below Level 76
- **WHEN** a player below level 76 attempts to equip a Royal Dynasty weapon
- **THEN** the system prevents equipping the item due to level requirement

### Requirement: Masterwork Passive Skills (`93101` - `93112`)
The game server SHALL attach Masterwork passive skills (`93101` to `93112`) to Royal Dynasty Weapons corresponding to their suffix (Great Gale, Destruction, Thunder, Earth, Storm, Gale, Earthquake, Shadow, Nature, Wisdom, Chaos, Breeze).

#### Scenario: Active Masterwork Passive Effect
- **WHEN** a player equips a Royal Dynasty weapon with a Masterwork suffix
- **THEN** the server applies the corresponding passive skill (e.g. Earth grants +15% Blow Success Rate, Storm grants +50 Attack Range, Great Gale grants +5% Atk.Spd)

### Requirement: 3 Selectable SA Options and PvP Refine Triggers (`93130` - `93138`)
The game server SHALL support 3 SA choices per weapon (e.g. Focus, Health, Haste, Acumen, Empower, Magic Silence) and apply PvP refinement triggers (`93130` to `93138`) including +5% PvP Dmg & +500 CP (`93130`), Cancel (`93132`), Ignore Shield (`93133`), CP Drain (`93131`), Casting Speed Burst (`93135`), Rapid Fire (`93136`), and Mirage (`93138`).

#### Scenario: Triggering PvP Refine Effect in Combat
- **WHEN** a player wielding a PvP refined Royal Dynasty weapon hits an opponent in PvP combat
- **THEN** the server triggers the weapon's unique PvP effect (e.g. Cancel strips 1-2 buffs, Mirage forces target loss, or CP Drain drains opponent CP)

### Requirement: Client Data DAT Integration
The project SHALL provide formatted client DAT file entries (`itemname-e.dat` and `Weapongrp.dat`) allowing client UI to display "Royal Dynasty [Weapon Name] - [Suffix]" names, descriptions, 3D mesh models, and icon textures matching Dynasty weapons.

#### Scenario: Inspecting weapon in client inventory
- **WHEN** a player views a Royal Dynasty weapon in their client inventory
- **THEN** the client renders the name "Royal Dynasty [Weapon Name] - [Suffix]" with Dynasty icon and 3D weapon model

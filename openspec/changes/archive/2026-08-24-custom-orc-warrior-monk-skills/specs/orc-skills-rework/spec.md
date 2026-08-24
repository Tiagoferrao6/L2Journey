## ADDED Requirements

### Requirement: Frenzy Skill Custom Rework (HP ≤ 40%, +300% P. Atk Unrestricted)
The game server SHALL process skill `Frenzy` (ID 176) with an activation threshold of `HP ≤ 40%` and grant a `+300% P. Atk` multiplier (`val="4.0"`) to all weapon types without requiring a 2-handed sword or blunt weapon.

#### Scenario: Verify Frenzy activation and damage boost across weapon types
- **WHEN** a player uses Frenzy with HP at or below 40%
- **THEN** Frenzy activates successfully and increases P. Atk by +300% whether equipping Bows, Daggers, Swords, Spears, Fists, or Polearms.

### Requirement: Guts Skill Custom Rework (HP ≤ 40%, +300% P. Def)
The game server SHALL process skill `Guts` (ID 139) with an activation threshold of `HP ≤ 40%` and grant a `+300% P. Def` multiplier (`val="4.0"`).

#### Scenario: Verify Guts activation and P. Def multiplier
- **WHEN** a player uses Guts with HP at or below 40%
- **THEN** Guts activates successfully and quadruples (4.0x / +300%) the player's physical defense.

### Requirement: Fury Fists Skill Custom Rework (+33% Atk. Spd., Unrestricted Weapon)
The game server SHALL process skill `Fury Fists` (ID 222) with a `+33% Atk. Spd.` multiplier (`val="1.33"`) applicable without weapon type restrictions.

#### Scenario: Verify Fury Fists toggle speed bonus
- **WHEN** a player activates Fury Fists
- **THEN** the character gains +33% Atk. Spd. regardless of equipped weapon type.

### Requirement: Tyrant Spirit Totems Weapon Freedom and 5-Minute Duration
The game server SHALL remove `DUALFIST` weapon restrictions from all 7 Tyrant Spirit Totems (`Wolf`, `Bear`, `Puma`, `Ogre`, `Rabbit`, `Bison`, `Hawk`) and set a uniform 5-minute duration (`abnormalTime = 300`).

#### Scenario: Verify Totem duration and weapon freedom
- **WHEN** a player uses any Tyrant Spirit Totem
- **THEN** the Totem applies its stat bonuses to all equipped weapon types and lasts for 300 seconds (5 minutes).

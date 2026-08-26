## ADDED Requirements

### Requirement: Expanded Combat Stat Caps for Players
The game server SHALL configure and enforce expanded combat stat limits for P. Atk, M. Atk, P. Atk Speed, and M. Atk Speed in `character.ini`.

#### Scenario: Verify expanded P. Atk and M. Atk caps
- **WHEN** a player's physical or magic attack exceeds 37,850 or 50,000 via buffs/gear
- **THEN** the character's P. Atk and M. Atk scale up to 100,000 before capping.

#### Scenario: Verify expanded Attack Speed and Cast Speed caps
- **WHEN** a player's physical attack speed or magic cast speed exceeds 1,400 or 1,850
- **THEN** the character's Attack Speed and Cast Speed scale up to 3,500 before capping.

### Requirement: Expanded EXP and SP Bonus Multiplier Caps
The game server SHALL configure and enforce an expanded maximum EXP/SP bonus multiplier of `5.0x` (500%) in `character.ini`.

#### Scenario: Verify EXP and SP bonus cap at 5.0x
- **WHEN** a player's combined EXP or SP bonuses (vitality, runes, events) exceed 3.5x up to 5.0x
- **THEN** the total bonus experience and SP multiplier scales up to 5.0x before capping.

### Requirement: Server Base Rates Rework (5x Base, 1.2x Party EXP, 10x Quest Rewards)
The game server SHALL configure base server multipliers in `rates.ini` for 5x XP/SP/Drop/Adena, 1.2x Party EXP (+20% bonus), and 10x Quest drop and rewards.

#### Scenario: Verify 5x XP/SP/Drop/Adena rates
- **WHEN** a player kills monsters or collects Adena/Drops
- **THEN** the server applies a 5x multiplier to XP, SP, Drop Amount, Spoil Amount, and Adena (Item ID 57).

#### Scenario: Verify 1.2x Party EXP bonus and 10x Quest rewards
- **WHEN** players form a party or complete quests
- **THEN** party members receive a 1.2x (+20%) EXP multiplier, and quest drop/rewards yield a 10x multiplier.

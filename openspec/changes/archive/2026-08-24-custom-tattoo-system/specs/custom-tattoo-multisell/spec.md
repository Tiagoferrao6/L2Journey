# Capability: Custom Tattoo Multisell Exchange

## ADDED Requirements

### Requirement: Multisell Purchase with Conqueror's Badge
The multisell file `dist/game/data/multisell/900003.xml` MUST allow purchasing Level 1 Tattoos (Right and Left) for 20 `Conqueror's Badge` coins (Item ID `99000`).

#### Scenario: Purchasing Lv 1 Tattoo
- **GIVEN** a player has 20 `Conqueror's Badge` items in inventory
- **WHEN** the player interacts with NPC `90000` and selects a Lv 1 Tattoo in multisell `900003`
- **THEN** 20 `Conqueror's Badge` items MUST be consumed and 1 Lv 1 Tattoo item delivered to inventory.

### Requirement: Multisell Progressive Upgrades
The multisell file `dist/game/data/multisell/900003.xml` MUST provide upgrade recipes from Level 1 up to Level 6 using the previous Level Tattoo item plus the required count of `Conqueror's Badge` coins.

#### Scenario: Upgrading Lv 5 to Lv 6 Tattoo
- **GIVEN** a player possesses a Lv 5 Tattoo item and 1,200 `Conqueror's Badge` coins
- **WHEN** the player executes the Lv 6 upgrade in multisell `900003`
- **THEN** the Lv 5 Tattoo item and 1,200 `Conqueror's Badge` coins MUST be consumed, and the corresponding Lv 6 Tattoo item delivered.

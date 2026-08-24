# custom-tattoo-items Specification

## Purpose
TBD - created by archiving change custom-tattoo-system. Update Purpose after archive.
## Requirements
### Requirement: Custom Tattoo Datapack Items Definition
The server MUST define 84 custom tattoo items in `dist/game/data/stats/items/custom/tattoos.xml` with IDs ranging from `41001` to `41084`.

#### Scenario: Item Registration and Attribute Modifiers
- **GIVEN** the game server starts up
- **WHEN** item stats are loaded from `dist/game/data/stats/items/custom/tattoos.xml`
- **THEN** all 84 custom tattoo items MUST be registered as Armor type items with appropriate stats and Henna Dye icons (`icon.etc_*_hena_i00` to `i02`).

### Requirement: Henna Dye Icon Progression
Each tattoo level MUST use the corresponding Henna Dye icon progression:
- Level 1 & 2: `icon.etc_*_hena_i00`
- Level 3 & 4: `icon.etc_*_hena_i01`
- Level 5 & 6: `icon.etc_*_hena_i02`

#### Scenario: Displaying Tattoo Icons in Inventory
- **GIVEN** a player views a Level 5 Tattoo of Flame in inventory
- **WHEN** the item icon is rendered
- **THEN** the icon MUST display `icon.etc_int_hena_i02`.

### Requirement: Equipment Slot Restrictions
Tattoo items MUST be restricted to equipment slots:
- Slot Right: `underwear` (IDs `41001` to `41042`)
- Slot Left: `hair2` (IDs `41043` to `41084`)

#### Scenario: Equipping Right and Left Tattoos
- **GIVEN** a player character in-game
- **WHEN** the player equips a Right Tattoo (e.g. ID `41001`) and a Left Tattoo (e.g. ID `41043`)
- **THEN** the Right Tattoo MUST equip into the `underwear` slot and the Left Tattoo MUST equip into the `hair2` slot without overriding each other.

### Requirement: Tattoo of Blood Balance and Penalties
The `Tattoo of Blood` items (Right: IDs `41019` to `41024`, Left: IDs `41061` to `41066`) MUST apply `absorbDam` (+3% to +20%), `maxHp` (+8% to +20% starting at Lv 3), `cAtk` (+15%/+20% at Lv 5/6), `gainHp` (+15%/+20% at Lv 5/6), and scaled `pDef` penalty (-1.5%, -2.5%, -4.0%, -5.5%, -7.5%, -10.0%).

#### Scenario: Equipping Tattoo of Blood Lv 6
- **GIVEN** a player equips a Tattoo of Blood Lv 6 (ID `41024` or `41066`)
- **WHEN** stats are recalculated
- **THEN** the character MUST gain +20% Vampiric (`absorbDam`), +20% `maxHp`, +20% `cAtk`, +20% `gainHp`, and MUST incur a `-10.0%` `pDef` penalty.


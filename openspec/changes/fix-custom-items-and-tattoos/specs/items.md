# Item and Equipment Specifications

## 1. Tattoos Slots
- **Right Tattoos** (IDs 41001-41042): MUST equip in the `underwear` slot.
- **Left Tattoos** (IDs 41043-41084): MUST equip in the `lbracelet` (Left Bracelet) slot.
- Both Server XML (`tattoos.xml`) and Client DAT (`armorgrp.txt`) must reflect these slot mappings.

## 2. Item Descriptions
- **Armors and Weapons**: Custom armors (99200-99224) and weapons (99300-99315) MUST have their names and descriptions properly populated in `itemname-e.txt` based on the items they were cloned from.
- **Tattoos**: Tattoo descriptions MUST include their status effects (e.g., "Increases P.Atk", "Increases Max HP") dynamically or statically formatted in the client.

## 3. Character Creation
- **Test Characters**: `SilverTester` and `TitanTester` MUST spawn with their custom weapons equipped (inserted into the `items` table with `loc = 'PAPERDOLL'`).

## 4. Raid Coin
- **Gold Einhasad** (ID 4356): MUST be renamed to "Raid Coin" in the client's `itemname-e.txt`.

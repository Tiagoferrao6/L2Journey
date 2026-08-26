# Design: Fix SilverTester Class and Equipment

## Technical Strategy
1. **Database Update Queries**:
   - Update `characters` table: `UPDATE characters SET classid = 106, base_class = 106 WHERE charId = 300000000;`
   - Update `character_subclasses` table:
     - `UPDATE character_subclasses SET class_id = 106 WHERE charId = 300000000 AND class_index = 0;`
     - `UPDATE character_subclasses SET class_id = 99 WHERE charId = 300000000 AND class_index = 1;`
   - Reset & Re-equip `items` table for `SilverTester` (`owner_id = 300000000`):
     - Equip Royal Light Armor set (`99210`, `99211`, `99212`, `99213`, `99214`, `99224`) and Royal Dynasty Bow (`99303`).
     - Insert Royal Dynasty Dual Sword (`99311`) into inventory for Sword Muse.

2. **Repository SQL File Update**:
   - Edit `dist/db_installer/sql/game/custom_test_characters_setup.sql`:
     - Change class ID from `102` to `106` for `SilverTester`.
     - Update subclass 1 to `99` (Sword Muse).
     - Update item definitions and paperdoll assignments to match Royal Light Set and Royal Dynasty Bow.

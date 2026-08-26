# Tasks: Fix SilverTester Class and Equipment

- [x] Execute MariaDB update query for `SilverTester` (`classid = 106`, `base_class = 106`)
- [x] Update `character_subclasses` for `SilverTester` (`index 0 = 106`, `index 1 = 99`)
- [x] Update `items` table for `SilverTester` to equip Royal Light Set (`99210-99214`, `99224`) and Royal Dynasty Bow (`99303`)
- [x] Add Royal Dynasty Dual Sword (`99311`) to `SilverTester` inventory
- [x] Update `dist/db_installer/sql/game/custom_test_characters_setup.sql` with class `106`, subclass `99`, and correct Royal Light set items
- [x] Verify MariaDB database state for `SilverTester`

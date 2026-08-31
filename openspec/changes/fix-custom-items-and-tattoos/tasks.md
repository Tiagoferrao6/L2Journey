# Tasks

## 1. Modify `fix_client_dat.py`
- [x] Add Left tattoos (41043-41084) to `TATTOO_ICONS` and `TATTOO_NAMES`.
- [x] In `fix_grp_file()` (`armorgrp.txt`), map Left tattoos to a base item that uses the `lbracelet` bodypart, or patch the bodypart column directly.
- [x] In `fix_itemname()`, add logic to extract base names and descriptions for `ARMORS_MAP` and `WEAPONS_MAP` to write them to `itemname-e.txt`.
- [x] In `fix_itemname()`, dynamically append status effects to tattoo descriptions.
- [x] In `fix_itemname()`, override the name of ID `4356` to "Raid Coin".

## 2. Update Server Data & Setup Scripts
- [x] Update `dist/game/data/stats/items/custom/tattoos.xml` to ensure Left tattoos have `<set name="bodypart" val="lbracelet" />` (if they don't already).
- [x] Modify `dist/db_installer/sql/game/z_custom_test_characters_setup.sql` to explicitly equip weapons to the test characters on creation.
- [x] Run `generate_full_sql.py` if necessary to rebuild the full SQL setup.

## 3. Deployment & Testing
- [x] Run `fix_client_dat.py` to regenerate the `client_dat/` text files.
- [ ] Compile the updated text files into `client_dat/*.dat` using L2FileEdit / `l2encdec.exe` (User action).
- [ ] Reload/Restart the GameServer to load the new `tattoos.xml` and apply the SQL changes (User action).
- [ ] Log in and verify that Left and Right tattoos can be equipped simultaneously, custom names/descriptions appear, "Raid Coin" is visible, and the test characters log in with weapons properly equipped in their hands.

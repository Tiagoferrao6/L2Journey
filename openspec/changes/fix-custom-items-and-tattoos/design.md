# Design

## 1. fix_client_dat.py Updates
### Tattoos Slots
- Expand `TATTOO_ICONS` and `TATTOO_NAMES` to include the Left tattoos (IDs 41043-41084).
- In `fix_grp_file()` for `armorgrp.txt`:
  - When generating a Right tattoo (41001-41042), clone base ID `684` (`underwear`).
  - When generating a Left tattoo (41043-41084), clone base ID `6408` (Formal Wear) or explicitly change the bodypart ID in the tab-separated string. The `bodypart` column is index 10 in CT2.6 `armorgrp.txt`. `underwear` is `0` (or `6`?), `lbracelet` is `4`. Wait, it is safer to just modify the specific column index for the `bodypart`.
  
### Item Names & Descriptions
- **Armors/Weapons**: In `fix_itemname()`, we need to read the original `itemname-e.txt`, extract the original name and description for each base item, and when writing the new custom IDs (99200+ and 99300+), insert the cloned name/description instead of skipping them.
- **Tattoos**: Define a stats string for each tattoo type and level, and append it to the `desc` variable in `fix_itemname()`.
- **Raid Coin**: Check for ID `4356` in `fix_itemname()` and replace its name field with `Raid Coin`.

## 2. Character Setup Scripts
- Update `dist/db_installer/sql/game/z_custom_test_characters_setup.sql`.
- Add SQL `INSERT` statements to give `TitanTester` and `SilverTester` the custom weapons (`99300`-`99315`).
- Ensure they are equipped by setting `loc = 'PAPERDOLL'` and the correct `loc_data`.

## 3. Server Configuration (Deployment)
- The user must run the SQL update and rebuild/restart the server or manually copy the updated `tattoos.xml` and SQL changes into their Docker container, since `./dist/game/data` is not volume-mounted in `docker-compose.yml`.

## 1. Item Generation

- [x] 1.1 Add the new custom item `Golkonda Horn` (ID 99900) to the item arrays in the python tools.
- [x] 1.2 Create the XML definition for the item in `dist/game/data/stats/items/custom/horns.xml`.
- [x] 1.3 Run `fix_client_dat.py` to map the new item into the client `.txt` files.

## 2. Golkonda Multiverse (Bosses & Drops)

- [x] 2.1 Create custom NPC templates in XML for Tier 1 Exiled Golkonda (ID 29000) and Tier 3 Infernal Golkonda (ID 29001), tweaking HP, P.Atk, and M.Atk.
- [x] 2.2 Add the custom droplists to these bosses (25% for Tier 1, 100% for 1-5 horns on Tier 3) and append the drop to the original Tier 2 Golkonda (50% chance).
- [x] 2.3 Create an SQL script to spawn ID 29000 in The Cemetery and ID 29001 in the Monastery of Silence.

## 3. Quest and NPC Implementation

- [x] 3.1 Create a Custom NPC (Cumulative Manager) with HTML dialogs to introduce the system.
- [x] 3.2 Implement the core logic (Bypasses or Python Quest Script) that consumes the `Golkonda Horn`.
- [x] 3.3 Implement the filtering logic to only display/apply Cumulative Subclasses that match the character's active class race.
- [x] 3.4 Ensure the script safely executes the `UPDATE character_subclasses SET dual_class_id = ?` query.

## 4. Final Validation

- [x] 4.1 Compile the `itemname-e.dat` using L2FileEdit (User action).
- [x] 4.2 Restart GameServer to load new XMLs and Python scripts (User action).
- [x] 4.3 Test defeating each Golkonda tier and applying the Cumulative Subclass.

# Proposal: Fix Custom Items, Tattoos, and Character Setup

## Context
After logging into the test server, several discrepancies were found between the intended custom item designs and their in-game implementations:
1. Custom tattoos are occupying the same `underwear` slot, making it impossible to equip both a Right and Left tattoo simultaneously.
2. Custom armors and weapons (Royal Dynasty) lack names and descriptions in the client because they were omitted from the text injection script.
3. The custom weapons do not appear equipped on the test characters upon creation.
4. The "Raid Coin" is still named "Gold Einhasad" and needs to be properly renamed in the client files.

## Problem
- `fix_client_dat.py` mapped all tattoos to base ID `684` (`underwear`) and didn't generate Left tattoos (IDs 41043+).
- `fix_client_dat.py` did not copy base item names and descriptions for `ARMORS_MAP` and `WEAPONS_MAP` into `itemname-e.txt`.
- The character setup SQL script (`z_custom_test_characters_setup.sql`) might not properly insert or equip the weapons into the `items` table with the correct `loc` (PAPERDOLL).
- "Gold Einhasad" (ID 4356) remains unchanged in `itemname-e.txt`.

## Goal
Fix the item slots so tattoos can be worn simultaneously, add missing names/descriptions (including stats in tattoo descriptions), ensure the test characters spawn with their weapons equipped, and rename Gold Einhasad to Raid Coin.

## Scope
1. Update `tattoos.xml` and `fix_client_dat.py` so Left Tattoos use the `lbracelet` slot instead of `underwear`.
2. Update `fix_client_dat.py` to copy names and descriptions for custom armors and weapons.
3. Include tattoo stats in their generated descriptions in `fix_client_dat.py`.
4. Review and patch `z_custom_test_characters_setup.sql` to ensure Royal Dynasty Weapons are granted and equipped.
5. Add logic to rename Gold Einhasad (ID 4356) to "Raid Coin" in `fix_client_dat.py`.

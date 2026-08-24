## 1. Custom Weapon Stats & Item Definitions

- [x] 1.1 Create `dist/game/data/stats/items/custom/royal_dynasty_weapons.xml` with 16 Royal Dynasty S Grade (Level 76+) weapon variants (Blade, Guardian, Crusher, Cudgel, Baghnakh, Knife, Bow, Crossbow, Halberd, Dual Blade, Dual Daggers, Phantom, Mace, Staff, Rapier, Ancient Sword).
- [x] 1.2 Create `dist/game/data/stats/skills/custom/royal_dynasty_weapon_skills.xml` with Masterwork passive skills (`93101` to `93112`), 3 SA options per weapon, and PvP refine triggers (`93130` to `93138`).

## 2. NPC Multisell Exchange Integration

- [x] 2.1 Create multisell XML `dist/game/data/multisell/custom/99300.xml` allowing players to purchase Royal Dynasty Weapons using Conqueror's Badges (Item ID 99000).
- [x] 2.2 Link multisell list to custom NPC HTML/dialogue (`dist/game/data/html/custom/99000.htm`).

## 3. Client DAT File Integration & Documentation

- [x] 3.1 Generate DAT file entries for `itemname-e.dat` for all 16 Royal Dynasty Weapons.
- [x] 3.2 Generate DAT file entries for `Weapongrp.dat` pointing to Dynasty weapon meshes and icons for all 16 Royal Dynasty Weapons.

## 4. Verification & Testing

- [x] 4.1 Verify XML schema validity for all custom XMLs.
- [x] 4.2 Test level 76 requirement, spawn, equip, Masterwork passives, SA activation, PvP refine triggers, and multisell exchange in game server environment.

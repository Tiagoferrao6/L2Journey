## 1. Custom Stats & Item Definitions

- [x] 1.1 Create `dist/game/data/stats/items/custom/royal_set.xml` with Royal S Grade (Level 76+) Breastplate variants, Gaiters, Helmet, Gauntlets, Boots, Shield, Sigil, and **Royal Cloak (Capa Royal)**.
- [x] 1.2 Create `dist/game/data/stats/skills/custom/royal_set.xml` with custom passive skills for each Option A class specialization set bonus, enchant +6 skills, and active **Royal Resurrection (70% XP)** skill (Skill `99220`).
- [x] 1.3 Create `dist/game/data/stats/armorsets/royal_set.xml` mapping set IDs to royal item parts and skills.

## 2. NPC Multisell Exchange Integration

- [x] 2.1 Create `dist/game/data/multisell/custom/99200.xml` allowing players to purchase Royal Set items and Royal Cloak using Conqueror's Badges (Item ID 99000).
- [x] 2.2 Link multisell list to custom NPC HTML/dialogue or shop.

## 3. Client DAT File Integration & Documentation

- [x] 3.1 Generate DAT file entries for `itemname-e.dat` for all Royal Set items and Royal Cloak.
- [x] 3.2 Generate DAT file entries for `ArmorGrp.dat` pointing to Dynasty meshes/textures and cloak model for all Royal Set items.

## 4. Verification & Testing

- [x] 4.1 Verify XML schema validity for all custom XMLs.
- [x] 4.2 Test level 76 requirement, spawn, equip, Royal Cloak 70% XP Resurrection skill, set bonus skill activation, and multisell exchange in game server environment.

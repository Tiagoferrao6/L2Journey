# Tasks: Custom Rework for Frenzy, Guts, Fury Fists, and Spirit Totems

## Implementation Tasks

- [x] 1. **Frenzy & Guts XML Update**
  - Update `dist/game/data/stats/skills/00100-00199.xml` for `Frenzy` (ID 176): Set `<player hp="40" />`, set P. Atk multiplier to `4.0` (+300%), remove `<using kind="SWORD,BLUNT" />` and `<using slot="lrhand" />` conditions.
  - Update `dist/game/data/stats/skills/00100-00199.xml` for `Guts` (ID 139): Set `<player hp="40" />`, set P. Def multiplier to `4.0` (+300%).

- [x] 2. **Fury Fists XML Update**
  - Update `dist/game/data/stats/skills/00200-00299.xml` for `Fury Fists` (ID 222): Set Atk. Spd. multiplier to `1.33` (+33%) and remove weapon restrictions.

- [x] 3. **Tyrant Spirit Totems XML Rework**
  - Update `dist/game/data/stats/skills/00000-00099.xml`: Remove `DUALFIST` restrictions and set `abnormalTime = 300` for Wolf (`83`) and Bear (`76`) Spirit Totems.
  - Update `dist/game/data/stats/skills/00100-00199.xml`: Remove `DUALFIST` restrictions and set `abnormalTime = 300` for Ogre (`109`) Spirit Totem.
  - Update `dist/game/data/stats/skills/00200-00299.xml`: Remove `DUALFIST` restrictions and set `abnormalTime = 300` for Puma (`282`), Rabbit (`298`), and Bison (`292`) Spirit Totems.
  - Update `dist/game/data/stats/skills/00400-00499.xml`: Remove `DUALFIST` restrictions and set `abnormalTime = 300` for Hawk (`425`) Spirit Totem.

- [x] 4. **Validation & Verification**
  - Reload skill XML definitions in GameServer (`//reload skill` or container reload).
  - Verify skill values on test character (`TitanTester` / `SilverTester`).

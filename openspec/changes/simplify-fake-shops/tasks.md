## 1. Clean Database
- [x] 1.1 Execute SQL script to delete all old fake shops from the `characters` table (`GiranSup%`, `DionTrader%`, `AdenElite%`, `RuneShop%`, `OrenShop%`, `HuntersShop%`, `HeineShop%`, `GoddardShop%`, `SchuttgartShop%`, `GiranMat%`, `GiranEquip%`, `GiranTop%`).
- [x] 1.2 Update `dist/db_installer/sql/game/z_custom_test_characters_setup.sql` to only keep the 15 dwarves.

## 2. Update XML Configuration
- [x] 2.1 Edit `dist/game/data/fakeplayers/fake_shops.xml` and remove all shops except the 15 Gludio dwarves.
- [x] 2.2 Reorganize the 15 dwarves in `fake_shops.xml` into the following profiles:
    - **Dynamic Sellers**: Gimli (Materials), Thorin (Supplies), Durin (Items).
    - **Fixed Sellers**: Balin, Dwalin, Fili, Kili.
    - **Buyers**: Oin, Gloin, Bifur, Bofur.
    - **Crafters**: Bombur, Nori, Ori, Dori.

## 3. Verify
- [ ] 3.1 Start GameServer and verify only 15 shops spawn in Gludio, successfully representing all behaviors without errors.

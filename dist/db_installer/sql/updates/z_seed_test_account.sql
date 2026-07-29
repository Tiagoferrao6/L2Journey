-- Create Test Account: tester / tester
INSERT IGNORE INTO `accounts` (`login`, `password`, `accessLevel`) 
VALUES ('tester', '40bd001563085fc35165329ea1ff5c5ecbdbbefa', 0);

-- Create Test Character: KaelTester (Level 40 Gladiator in Town of Gludio)
INSERT IGNORE INTO `characters` 
(`account_name`, `charId`, `char_name`, `level`, `maxHp`, `curHp`, `maxCp`, `curCp`, `maxMp`, `curMp`, `face`, `hairStyle`, `hairColor`, `sex`, `heading`, `x`, `y`, `z`, `exp`, `sp`, `karma`, `pvpkills`, `pkkills`, `race`, `classid`, `base_class`)
VALUES 
('tester', 268435457, 'KaelTester', 40, 1500, 1500, 800, 800, 600, 600, 0, 0, 0, 0, 0, -12787, 122779, -3112, 15000000, 500000, 0, 0, 0, 0, 9, 9);

-- Create Inventory Items & Equipment
-- 1. Adena (5,000,000)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000001, 57, 5000000, 0, 'INVENTORY', 0);

-- 2. Soulshots C-Grade (10,000)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000002, 1464, 10000, 0, 'INVENTORY', 0);

-- 3. Greater Healing Potion (500)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000003, 1061, 500, 0, 'INVENTORY', 0);

-- 4. Scroll of Escape (20)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000004, 736, 20, 0, 'INVENTORY', 0);

-- 5. Dual Samurai Longsword * Samurai Longsword (Equipped)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000005, 2626, 1, 0, 'PAPERDOLL', 8);

-- 6. Full Plate Armor (Equipped)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000006, 356, 1, 0, 'PAPERDOLL', 10);

-- 7. Full Plate Helmet (Equipped)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000007, 2414, 1, 0, 'PAPERDOLL', 7);

-- 8. Full Plate Gauntlets (Equipped)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000008, 2459, 1, 0, 'PAPERDOLL', 11);

-- 9. Full Plate Boots (Equipped)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000009, 2439, 1, 0, 'PAPERDOLL', 12);

-- 10. Aquamarine Ring #1 & #2 (Equipped)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000010, 875, 1, 0, 'PAPERDOLL', 5);
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000011, 875, 1, 0, 'PAPERDOLL', 6);

-- 11. Moonstone Earring #1 & #2 (Equipped)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000012, 847, 1, 0, 'PAPERDOLL', 2);
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000013, 847, 1, 0, 'PAPERDOLL', 3);

-- 12. Necklace of Mermaid (Equipped)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000014, 906, 1, 0, 'PAPERDOLL', 4);

-- 13. Trade Materials for WTB Trader Testing
-- Animal Bone (1,500)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000015, 1872, 1500, 0, 'INVENTORY', 0);
-- Iron Ore (800)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000016, 1869, 800, 0, 'INVENTORY', 0);
-- Coal (500)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000017, 1870, 500, 0, 'INVENTORY', 0);
-- Varnish (300)
INSERT IGNORE INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000018, 1865, 300, 0, 'INVENTORY', 0);

-- Gladiator Level 40 Core Skills
-- Triple Slash (Skill 1, Level 1)
INSERT IGNORE INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 1, 1, 0);
-- Sonic Buster (Skill 6, Level 1)
INSERT IGNORE INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 6, 1, 0);
-- Dual Weapon Master (Skill 141, Level 1)
INSERT IGNORE INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 141, 1, 0);
-- Heavy Armor Mastery (Skill 231, Level 1)
INSERT IGNORE INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 231, 1, 0);

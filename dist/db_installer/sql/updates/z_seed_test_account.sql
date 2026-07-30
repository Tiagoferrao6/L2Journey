-- Create Test Account: tester / tester
INSERT INTO `accounts` (`login`, `password`, `accessLevel`) 
VALUES ('tester', 'q02NKl9IChNwZ9oXEAJxzRdmB6E=', 0)
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`);

-- Clean up any existing data for test character ID 268435457
DELETE FROM `characters` WHERE `charId` = 268435457;
DELETE FROM `items` WHERE `owner_id` = 268435457;
DELETE FROM `character_skills` WHERE `charId` = 268435457;

-- Create Test Character: KaelTyrant (Level 40 Tyrant in Town of Gludio)
INSERT INTO `characters` 
(`account_name`, `charId`, `char_name`, `level`, `maxHp`, `curHp`, `maxCp`, `curCp`, `maxMp`, `curMp`, `face`, `hairStyle`, `hairColor`, `sex`, `heading`, `x`, `y`, `z`, `exp`, `sp`, `karma`, `pvpkills`, `pkkills`, `race`, `classid`, `base_class`)
VALUES 
('tester', 268435457, 'KaelTyrant', 40, 1800, 1800, 950, 950, 500, 500, 0, 0, 0, 0, 0, -14347, 123622, -3112, 15000000, 500000, 0, 0, 0, 3, 48, 48);

-- Create Inventory Items & Equipment
-- 1. Adena (5,000,000)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000001, 57, 5000000, 0, 'INVENTORY', 0);

-- 2. Soulshots C-Grade (10,000)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000002, 1464, 10000, 0, 'INVENTORY', 0);

-- 3. Greater Healing Potion (500)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000003, 1061, 500, 0, 'INVENTORY', 0);

-- 4. Scroll of Escape (20)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000004, 736, 20, 0, 'INVENTORY', 0);

-- 5. Great Pata (Equipped)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000005, 266, 1, 0, 'PAPERDOLL', 5);

-- 6. Plated Leather Armor (Equipped)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000006, 398, 1, 0, 'PAPERDOLL', 6);

-- 7. Plated Leather Gaiters (Equipped)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000007, 418, 1, 0, 'PAPERDOLL', 11);

-- 8. Plated Leather Gloves (Equipped)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000008, 2455, 1, 0, 'PAPERDOLL', 10);

-- 9. Plated Leather Boots (Equipped)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000009, 2431, 1, 0, 'PAPERDOLL', 12);

-- 10. Aquamarine Ring #1 & #2 (Equipped)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000010, 875, 1, 0, 'PAPERDOLL', 13);
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000011, 875, 1, 0, 'PAPERDOLL', 14);

-- 11. Moonstone Earring #1 & #2 (Equipped)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000012, 847, 1, 0, 'PAPERDOLL', 8);
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000013, 847, 1, 0, 'PAPERDOLL', 9);

-- 12. Necklace of Mermaid (Equipped)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000014, 906, 1, 0, 'PAPERDOLL', 4);

-- 13. Energy Stone (100)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000015, 5589, 100, 0, 'INVENTORY', 0);

-- 14. Trade Materials for WTB Trader Testing
-- Animal Bone (1,500)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000016, 1872, 1500, 0, 'INVENTORY', 0);
-- Iron Ore (800)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000017, 1869, 800, 0, 'INVENTORY', 0);
-- Coal (500)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000018, 1870, 500, 0, 'INVENTORY', 0);
-- Varnish (300)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000019, 1865, 300, 0, 'INVENTORY', 0);
-- Stem (2,000)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000020, 1864, 2000, 0, 'INVENTORY', 0);
-- Thread (2,000)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000021, 1868, 2000, 0, 'INVENTORY', 0);
-- Bronze Breastplate (2)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`)
VALUES (268435457, 900000022, 26, 2, 0, 'INVENTORY', 0);

-- Tyrant Level 40 Active & Passive Skills
-- 1. Active Skills
-- Puma Spirit Totem (Skill 282, Level 1)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 282, 1, 0);
-- Wolf Spirit Totem (Skill 83, Level 1)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 83, 1, 0);
-- Bear Spirit Totem (Skill 76, Level 1)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 76, 1, 0);
-- Focused Force (Skill 50, Level 3)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 50, 3, 0);
-- Force Blaster (Skill 54, Level 15)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 54, 15, 0);
-- Burning Fist (Skill 280, Level 3)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 280, 3, 0);
-- Soul Breaker (Skill 281, Level 3)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 281, 3, 0);
-- Hurricane Assault (Skill 284, Level 6)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 284, 6, 0);
-- Iron Punch (Skill 29, Level 24)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 29, 24, 0);
-- Stunning Fist (Skill 120, Level 15)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 120, 15, 0);
-- Cripple (Skill 95, Level 6)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 95, 6, 0);

-- 2. Passive Skills
-- Fist Weapon Mastery (Skill 210, Level 11)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 210, 11, 0);
-- Light Armor Mastery (Skill 233, Level 13)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 233, 13, 0);
-- Boost Attack Speed (Skill 168, Level 1)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 168, 1, 0);
-- Agile Movement (Skill 319, Level 2)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 319, 2, 0);
-- Force Mastery (Skill 993, Level 2)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 993, 2, 0);
-- Toughness (Skill 134, Level 1)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 134, 1, 0);
-- Grade Expertise C (Skill 239, Level 2)
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES (268435457, 239, 2, 0);

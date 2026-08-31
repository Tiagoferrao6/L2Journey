-- ---------------------------------------------------------------------------
-- Unified Test Characters Setup: SilverTester (300000000) & TitanTester (300000001)
-- ---------------------------------------------------------------------------

START TRANSACTION;

-- ===========================================================================
-- 1. ACCOUNT CREATION / VERIFICATION
-- ===========================================================================
INSERT IGNORE INTO `accounts` (`login`, `password`, `accessLevel`) VALUES
('tester', 'q02NKl9IChNwZ9oXEAJxzRdmB6E=', 0);

-- ===========================================================================
-- 2. CHARACTERS PROVISIONING (Gludio Town Center Coordinates x=-12787, y=122779, z=-3112)
-- ===========================================================================
REPLACE INTO `characters` (
  `account_name`, `charId`, `char_name`, `level`, `maxHp`, `curHp`, `maxCp`, `curCp`, `maxMp`, `curMp`,
  `face`, `hairStyle`, `hairColor`, `sex`, `heading`, `x`, `y`, `z`, `exp`, `sp`, `karma`, `fame`,
  `pvpkills`, `pkkills`, `clanid`, `race`, `classid`, `base_class`, `title`, `title_color`, `accesslevel`,
  `online`, `onlinetime`, `char_slot`, `newbie`, `clan_privs`, `nobless`, `createDate`
) VALUES
-- 2.1 SilverTester (charId = 300000000, Elf Female Moonlight Sentinel - Class 102)
(
  'tester', 300000000, 'SilverTester', 85, 12000, 12000, 8000, 8000, 4000, 4000,
  0, 0, 0, 1, 0, -12787, 122779, -3112, 6299999999, 2000000000, 0, 50000,
  100, 0, 100000, 1, 102, 102, 'Silver Hero', 15525282, 0,
  0, 36000, 0, 0, 16777215, 1, CURRENT_DATE()
),
-- 2.2 TitanTester (charId = 300000001, Orc Male Titan - Class 113)
(
  'tester', 300000001, 'TitanTester', 85, 15000, 15000, 10000, 10000, 5000, 5000,
  0, 0, 0, 0, 0, -12787, 122779, -3112, 6299999999, 2000000000, 0, 50000,
  100, 0, 100000, 3, 113, 113, 'Titan Hero', 15525282, 0,
  0, 36000, 1, 0, 16777215, 1, CURRENT_DATE()
);

-- ===========================================================================
-- 3. SUBCLASSES SETUP (Obrigatoriamente incluindo class_index = 0 para Base Class)
-- ===========================================================================
DELETE FROM `character_subclasses` WHERE `charId` IN (300000000, 300000001);

INSERT INTO `character_subclasses` (`charId`, `class_id`, `exp`, `sp`, `level`, `class_index`) VALUES
-- SilverTester (300000000)
(300000000, 102, 6299999999, 2000000000, 85, 0), -- Base Class: Moonlight Sentinel
(300000000, 99, 6299999999, 2000000000, 85, 1), -- Subclass 1: Eva's Templar
(300000000, 105, 6299999999, 2000000000, 85, 2), -- Subclass 2: Eva's Saint

-- TitanTester (300000001)
(300000001, 113, 6299999999, 2000000000, 85, 0), -- Base Class: Titan
(300000001, 89,  6299999999, 2000000000, 85, 1), -- Subclass 1: DreadNought
(300000001, 111, 6299999999, 2000000000, 85, 2), -- Subclass 2: Spectral Master
(300000001, 108, 6299999999, 2000000000, 85, 3); -- Subclass 3: Ghost Hunter

-- ===========================================================================
-- 4. HERO TABLE REGISTRATION & HERO SKILLS
-- ===========================================================================
REPLACE INTO `heroes` (`charId`, `class_id`, `count`, `played`, `claimed`, `message`) VALUES
(300000000, 106, 1, 1, 'true', 'Silver Hero Tester'),
(300000001, 113, 1, 1, 'true', 'Titan Hero Tester');

-- Hero Skills para ambos os personagens
DELETE FROM `character_skills` WHERE `charId` IN (300000000, 300000001) AND `skill_id` IN (395, 396, 1374, 1375, 1376);
INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES
-- SilverTester Hero Skills
(300000000, 395, 1, 0), (300000000, 396, 1, 0), (300000000, 1374, 1, 0), (300000000, 1375, 1, 0), (300000000, 1376, 1, 0),
-- TitanTester Hero Skills
(300000001, 395, 1, 0), (300000001, 396, 1, 0), (300000001, 1374, 1, 0), (300000001, 1375, 1, 0), (300000001, 1376, 1, 0);

-- ===========================================================================
-- 5. CLAN DATA & CLAN SKILLS PROVISIONING
-- ===========================================================================
INSERT IGNORE INTO `clan_data` (`clan_id`, `clan_name`, `clan_level`, `reputation_score`, `leader_id`) VALUES
(100000, 'TesterClan', 11, 100000, 300000001);

DELETE FROM `clan_skills` WHERE `clan_id` = 100000;
INSERT INTO `clan_skills` (`clan_id`, `skill_id`, `skill_level`, `skill_name`, `sub_pledge_id`) VALUES
(100000, 370, 1, 'Clan Imperium', -2),
(100000, 371, 1, 'Clan Life Stone', -2),
(100000, 372, 3, 'Clan Aegis', -2),
(100000, 373, 3, 'Clan Might', -2),
(100000, 374, 3, 'Clan Morale', -2),
(100000, 375, 3, 'Clan Clarity', -2),
(100000, 376, 3, 'Clan Empowerment', -2),
(100000, 377, 3, 'Clan Essence', -2),
(100000, 378, 3, 'Clan Guidance', -2),
(100000, 379, 3, 'Clan Agility', -2),
(100000, 380, 3, 'Clan March', -2),
(100000, 391, 1, 'Clan Unity', -2),
(100000, 392, 1, 'Clan Health', -2),
(100000, 393, 1, 'Clan Spirit', -2),
(100000, 394, 1, 'Clan Guidance', -2),
(100000, 611, 3, 'Clan Aegis', -2),
(100000, 612, 3, 'Clan Might', -2),
(100000, 613, 3, 'Clan Shield', -2),
(100000, 614, 3, 'Clan Cyclone', -2),
(100000, 615, 3, 'Clan Fortitude', -2),
(100000, 616, 3, 'Clan Freedom', -2),
(100000, 617, 3, 'Clan Vigilance', -2),
(100000, 618, 3, 'Clan March', -2),
(100000, 619, 3, 'Clan Agility', -2),
(100000, 620, 3, 'Clan Mastery', -2);

-- ===========================================================================
-- 6. CHARACTER SKILLS PROVISIONING
-- ===========================================================================
DELETE FROM `character_skills` WHERE `charId` IN (300000000, 300000001);

-- 6.1 SilverTester Skills (Moonlight Sentinel + Sword Muse Cumulative Songs)
INSERT IGNORE INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES
(300000000, 208, 52, 0),
(300000000, 209, 8, 0),
(300000000, 233, 47, 0),
(300000000, 16, 24, 0),
(300000000, 56, 24, 0),
(300000000, 58, 55, 0),
(300000000, 15, 52, 0),
(300000000, 312, 20, 0),
(300000000, 91, 2, 0),
(300000000, 113, 2, 0),
(300000000, 195, 1, 0),
(300000000, 173, 1, 0),
(300000000, 27, 5, 0),
(300000000, 198, 1, 0),
(300000000, 61, 3, 0),
(300000000, 21, 3, 0),
(300000000, 96, 2, 0),
(300000000, 256, 1, 0),
(300000000, 169, 2, 0),
(300000000, 225, 3, 0),
(300000000, 77, 2, 0),
(300000000, 111, 1, 0),
(300000000, 137, 1, 0),
(300000000, 99, 2, 0),
(300000000, 230, 2, 0),
(300000000, 101, 40, 0),
(300000000, 102, 16, 0),
(300000000, 171, 8, 0),
(300000000, 217, 45, 0),
(300000000, 232, 15, 0),
(300000000, 147, 51, 0),
(300000000, 153, 2, 0),
(300000000, 110, 1, 0),
(300000000, 28, 12, 0),
(300000000, 112, 2, 0),
(300000000, 191, 6, 0),
(300000000, 1405, 4, 0),
(300000000, 123, 3, 0),
(300000000, 269, 1, 0),
(300000000, 196, 1, 0),
(300000000, 267, 1, 0),
(300000000, 268, 1, 0),
(300000000, 270, 1, 0),
(300000000, 265, 1, 0),
(300000000, 98, 5, 0),
(300000000, 264, 1, 0),
(300000000, 402, 10, 0),
(300000000, 407, 10, 0),
(300000000, 266, 1, 0),
(300000000, 306, 1, 0),
(300000000, 304, 1, 0),
(300000000, 308, 1, 0),
(300000000, 305, 1, 0),
(300000000, 986, 25, 0),
(300000000, 988, 3, 0),
(300000000, 19, 37, 0),
(300000000, 303, 4, 0),
(300000000, 24, 31, 0),
(300000000, 415, 3, 0),
(300000000, 416, 3, 0),
(300000000, 413, 8, 0),
(300000000, 323, 1, 0),
(300000000, 324, 1, 0),
(300000000, 933, 1, 0),
(300000000, 194, 1, 0),
(300000000, 239, 7, 0),
(300000000, 1322, 1, 0),
(300000000, 1320, 9, 0),
(300000000, 141, 3, 0),
(300000000, 142, 5, 0),
(300000000, 3, 9, 0),
(300000000, 841, 1, 0),
(300000000, 842, 1, 0),
(300000000, 328, 1, 0),
(300000000, 343, 1, 0),
(300000000, 330, 1, 0),
(300000000, 354, 1, 0),
(300000000, 334, 1, 0),
(300000000, 369, 1, 0),
(300000000, 431, 1, 0),
(300000000, 533, 1, 0),
(300000000, 534, 1, 0),
(300000000, 459, 1, 0),
(300000000, 758, 1, 0),
(300000000, 759, 1, 0),
(300000000, 924, 1, 0),
(300000000, 946, 1, 0),
(300000000, 755, 1, 0),
(300000000, 756, 1, 0),
(300000000, 757, 1, 0),
(300000000, 772, 1, 0),
(300000000, 987, 1, 0),
(300000000, 990, 1, 0),
(300000000, 329, 1, 0),
(300000000, 349, 1, 0),
(300000000, 363, 1, 0),
(300000000, 364, 1, 0),
(300000000, 428, 1, 0),
(300000000, 437, 1, 0),
(300000000, 529, 1, 0),
(300000000, 455, 1, 0),
(300000000, 764, 1, 0),
(300000000, 766, 1, 0),
(300000000, 913, 1, 0),
(300000000, 914, 1, 0),
(300000000, 631, 1, 0),
(300000000, 632, 1, 0),
(300000000, 633, 1, 0),
(300000000, 650, 1, 0),
(300000000, 651, 1, 0);

-- 6.2 TitanTester Skills (Titan Main Class + DreadNought Cumulative Skills)
INSERT IGNORE INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES
(300000001, 257, 8, 0),
(300000001, 216, 45, 0),
(300000001, 227, 50, 0),
(300000001, 231, 50, 0),
(300000001, 255, 15, 0),
(300000001, 100, 15, 0),
(300000001, 245, 15, 0),
(300000001, 312, 20, 0),
(300000001, 211, 10, 0),
(300000001, 78, 1, 0),
(300000001, 212, 8, 0),
(300000001, 148, 2, 0),
(300000001, 256, 1, 0),
(300000001, 121, 6, 0),
(300000001, 75, 1, 0),
(300000001, 287, 3, 0),
(300000001, 210, 45, 0),
(300000001, 233, 47, 0),
(300000001, 29, 24, 0),
(300000001, 120, 15, 0),
(300000001, 95, 20, 0),
(300000001, 83, 1, 0),
(300000001, 319, 2, 0),
(300000001, 54, 49, 0),
(300000001, 50, 7, 0),
(300000001, 76, 1, 0),
(300000001, 284, 40, 0),
(300000001, 168, 3, 0),
(300000001, 993, 7, 0),
(300000001, 1405, 4, 0),
(300000001, 36, 37, 0),
(300000001, 48, 37, 0),
(300000001, 87, 1, 0),
(300000001, 317, 5, 0),
(300000001, 920, 37, 0),
(300000001, 116, 14, 0),
(300000001, 290, 14, 0),
(300000001, 286, 3, 0),
(300000001, 130, 2, 0),
(300000001, 104, 1, 0),
(300000001, 80, 1, 0),
(300000001, 181, 1, 0),
(300000001, 452, 5, 0),
(300000001, 88, 1, 0),
(300000001, 421, 5, 0),
(300000001, 422, 3, 0),
(300000001, 424, 3, 0),
(300000001, 320, 10, 0),
(300000001, 994, 1, 0),
(300000001, 280, 37, 0),
(300000001, 281, 37, 0),
(300000001, 282, 1, 0),
(300000001, 17, 34, 0),
(300000001, 222, 1, 0),
(300000001, 109, 1, 0),
(300000001, 35, 28, 0),
(300000001, 81, 3, 0),
(300000001, 420, 3, 0),
(300000001, 423, 3, 0),
(300000001, 461, 2, 0),
(300000001, 298, 1, 0),
(300000001, 292, 1, 0),
(300000001, 425, 1, 0),
(300000001, 194, 1, 0),
(300000001, 134, 1, 0),
(300000001, 239, 7, 0),
(300000001, 1322, 1, 0),
(300000001, 1320, 9, 0),
(300000001, 141, 3, 0),
(300000001, 142, 5, 0),
(300000001, 3, 9, 0),
(300000001, 226, 1, 0),
(300000001, 295, 1, 0),
(300000001, 841, 1, 0),
(300000001, 842, 1, 0),
(300000001, 56, 9, 0),
(300000001, 16, 9, 0),
(300000001, 328, 1, 0),
(300000001, 329, 1, 0),
(300000001, 921, 1, 0),
(300000001, 330, 1, 0),
(300000001, 359, 1, 0),
(300000001, 361, 1, 0),
(300000001, 339, 1, 0),
(300000001, 347, 1, 0),
(300000001, 360, 1, 0),
(300000001, 430, 1, 0),
(300000001, 440, 1, 0),
(300000001, 457, 1, 0),
(300000001, 758, 1, 0),
(300000001, 759, 1, 0),
(300000001, 767, 1, 0),
(300000001, 917, 1, 0),
(300000001, 755, 1, 0),
(300000001, 756, 1, 0),
(300000001, 757, 1, 0),
(300000001, 774, 1, 0),
(300000001, 995, 1, 0),
(300000001, 335, 1, 0),
(300000001, 362, 1, 0),
(300000001, 536, 1, 0),
(300000001, 456, 1, 0),
(300000001, 777, 1, 0),
(300000001, 631, 1, 0),
(300000001, 632, 1, 0),
(300000001, 633, 1, 0),
(300000001, 650, 1, 0),
(300000001, 651, 1, 0);

-- ===========================================================================
-- 7. ITEMS POPULATION (Calibrado para peso < 100%)
-- ===========================================================================
DELETE FROM `item_elementals` WHERE `itemId` IN (SELECT `object_id` FROM `items` WHERE `owner_id` IN (300000000, 300000001));
DELETE FROM `items` WHERE `owner_id` IN (300000000, 300000001);

SET @start_id = 900000000;

-- 7.1 SilverTester Items (300000000)
-- Consumables & Materials (INVENTORY)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000000, @start_id := @start_id + 1, 57, 1000000000, 0, 'INVENTORY', 0),    -- 1 Bi Adena
(300000000, @start_id := @start_id + 1, 4356, 500, 0, 'INVENTORY', 0),     -- 500 Raid Coins
(300000000, @start_id := @start_id + 1, 1467, 10000, 0, 'INVENTORY', 0),     -- Soulshot S
(300000000, @start_id := @start_id + 1, 3952, 10000, 0, 'INVENTORY', 0),     -- Blessed Spiritshot S
(300000000, @start_id := @start_id + 1, 1345, 20000, 0, 'INVENTORY', 0),     -- Shining Arrow (S-Grade)
(300000000, @start_id := @start_id + 1, 1785, 10000, 0, 'INVENTORY', 0),     -- Soul Ore
(300000000, @start_id := @start_id + 1, 3031, 10000, 0, 'INVENTORY', 0),     -- Spirit Ore
(300000000, @start_id := @start_id + 1, 1462, 5000, 0, 'INVENTORY', 0),      -- Crystal: S Grade
(300000000, @start_id := @start_id + 1, 9898, 1000, 0, 'INVENTORY', 0),      -- Battle Symbol (exemplo)
(300000000, @start_id := @start_id + 1, 5592, 1000, 0, 'INVENTORY', 0),      -- Greater CP Potion
(300000000, @start_id := @start_id + 1, 1539, 1000, 0, 'INVENTORY', 0),      -- Greater Healing Potion
(300000000, @start_id := @start_id + 1, 728, 1000, 0, 'INVENTORY', 0),       -- Mana Potion
(300000000, @start_id := @start_id + 1, 1538, 100, 0, 'INVENTORY', 0),      -- Blessed Scroll of Escape
(300000000, @start_id := @start_id + 1, 736, 100, 0, 'INVENTORY', 0),       -- Scroll of Escape
(300000000, @start_id := @start_id + 1, 4295, 1, 0, 'INVENTORY', 0),        -- Blooded Fabric
(300000000, @start_id := @start_id + 1, 3865, 1, 0, 'INVENTORY', 0),        -- Floating Stone
(300000000, @start_id := @start_id + 1, 7267, 1, 0, 'INVENTORY', 0),        -- Portal Stone
(300000000, @start_id := @start_id + 1, 99900, 10, 0, 'INVENTORY', 0),      -- Golkonda Horn

-- Tattoos
(300000000, @start_id := @start_id + 1, 41006, 1, 0, 'INVENTORY', 0), -- Ogre Lv 6
(300000000, @start_id := @start_id + 1, 41012, 1, 0, 'INVENTORY', 0), -- Monk Lv 6
(300000000, @start_id := @start_id + 1, 41018, 1, 0, 'INVENTORY', 0), -- Assassin Lv 6
(300000000, @start_id := @start_id + 1, 41024, 1, 0, 'INVENTORY', 0), -- Blood Lv 6

-- SilverTester EQUIPPED Gear (PAPERDOLL)
(300000000, @r_bow := @start_id + 1, 13467, 1, 6, 'PAPERDOLL', 14),       -- Vesper Thrower +6 (LRHAND)
(300000000, @r_chest := @start_id + 2, 15576, 1, 6, 'PAPERDOLL', 10),       -- Elegia Leather Breastplate +6 (CHEST)
(300000000, @r_legs := @start_id + 3, 15579, 1, 6, 'PAPERDOLL', 11),      -- Elegia Leather Legging +6 (LEGS)
(300000000, @r_head := @start_id + 4, 15573, 1, 6, 'PAPERDOLL', 6),       -- Elegia Leather Helmet +6 (HEAD)
(300000000, @r_gloves := @start_id + 5, 15582, 1, 6, 'PAPERDOLL', 9),      -- Elegia Leather Gloves +6 (GLOVES)
(300000000, @r_feet := @start_id + 6, 15585, 1, 6, 'PAPERDOLL', 12),      -- Elegia Leather Boots +6 (FEET)
(300000000, @start_id := @start_id + 7, 99224, 1, 6, 'PAPERDOLL', 13),      -- Royal Cloak +6 (BACK)

-- Joias Boss +6
(300000000, @start_id := @start_id + 1, 6657, 1, 6, 'PAPERDOLL', 3),        -- Valakas' Necklace (NECK)
(300000000, @start_id := @start_id + 1, 6656, 1, 6, 'PAPERDOLL', 1),        -- Antharas' Earring (REAR)
(300000000, @start_id := @start_id + 1, 6659, 1, 6, 'PAPERDOLL', 2),        -- Zaken's Earring (LEAR)
(300000000, @start_id := @start_id + 1, 6658, 1, 6, 'PAPERDOLL', 4),       -- Ring of Baium (RFINGER)
(300000000, @start_id := @start_id + 1, 6660, 1, 6, 'PAPERDOLL', 5);       -- Ring of Queen Ant (LFINGER)

-- Elementals for SilverTester Gear
INSERT INTO `item_elementals` (`itemId`, `elemType`, `elemValue`) VALUES
(@r_bow, 0, 300), (@r_chest, 0, 120), (@r_legs, 0, 120), (@r_head, 0, 120), (@r_gloves, 0, 120), (@r_feet, 0, 120);

-- 7.2 TitanTester Items (300000001)
-- Consumables & Materials (INVENTORY)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000001, @start_id := @start_id + 1, 57, 1000000000, 0, 'INVENTORY', 0),    -- 1 Bi Adena
(300000001, @start_id := @start_id + 1, 4356, 500, 0, 'INVENTORY', 0),     -- 500 Raid Coins
(300000001, @start_id := @start_id + 1, 1467, 10000, 0, 'INVENTORY', 0),     -- Soulshot S
(300000001, @start_id := @start_id + 1, 3952, 10000, 0, 'INVENTORY', 0),     -- Blessed Spiritshot S
(300000001, @start_id := @start_id + 1, 1785, 10000, 0, 'INVENTORY', 0),     -- Soul Ore
(300000001, @start_id := @start_id + 1, 3031, 10000, 0, 'INVENTORY', 0),     -- Spirit Ore
(300000001, @start_id := @start_id + 1, 1462, 5000, 0, 'INVENTORY', 0),      -- Crystal: S Grade
(300000001, @start_id := @start_id + 1, 9898, 1000, 0, 'INVENTORY', 0),      -- Battle Symbol
(300000001, @start_id := @start_id + 1, 5592, 1000, 0, 'INVENTORY', 0),      -- Greater CP Potion
(300000001, @start_id := @start_id + 1, 1539, 1000, 0, 'INVENTORY', 0),      -- Greater Healing Potion
(300000001, @start_id := @start_id + 1, 728, 1000, 0, 'INVENTORY', 0),       -- Mana Potion
(300000001, @start_id := @start_id + 1, 1538, 100, 0, 'INVENTORY', 0),      -- Blessed Scroll of Escape
(300000001, @start_id := @start_id + 1, 736, 100, 0, 'INVENTORY', 0),       -- Scroll of Escape
(300000001, @start_id := @start_id + 1, 4295, 1, 0, 'INVENTORY', 0),        -- Blooded Fabric
(300000001, @start_id := @start_id + 1, 3865, 1, 0, 'INVENTORY', 0),        -- Floating Stone
(300000001, @start_id := @start_id + 1, 7267, 1, 0, 'INVENTORY', 0),        -- Portal Stone

-- Tattoos
(300000001, @start_id := @start_id + 1, 41006, 1, 0, 'INVENTORY', 0), -- Ogre Lv 6
(300000001, @start_id := @start_id + 1, 41012, 1, 0, 'INVENTORY', 0), -- Monk Lv 6
(300000001, @start_id := @start_id + 1, 41018, 1, 0, 'INVENTORY', 0), -- Assassin Lv 6
(300000001, @start_id := @start_id + 1, 41024, 1, 0, 'INVENTORY', 0), -- Blood Lv 6

-- TitanTester EQUIPPED Gear & Custom Royal Items (PAPERDOLL)
(300000001, @t_weap := @start_id + 1, 13458, 1, 6, 'PAPERDOLL', 14),       -- Vesper Slasher +6 (LRHAND)
(300000001, @t_chest := @start_id + 2, 15575, 1, 6, 'PAPERDOLL', 10),       -- Elegia Breastplate +6 (CHEST)
(300000001, @t_legs := @start_id + 3, 15578, 1, 6, 'PAPERDOLL', 11),       -- Elegia Gaiter +6 (LEGS)
(300000001, @t_head := @start_id + 4, 15572, 1, 6, 'PAPERDOLL', 6),        -- Elegia Helmet +6 (HEAD)
(300000001, @t_gloves := @start_id + 5, 15581, 1, 6, 'PAPERDOLL', 9),       -- Elegia Gauntlet +6 (GLOVES)
(300000001, @t_feet := @start_id + 6, 15584, 1, 6, 'PAPERDOLL', 12),       -- Elegia Boots +6 (FEET)
(300000001, @start_id := @start_id + 7, 99224, 1, 6, 'PAPERDOLL', 13),       -- Royal Cloak +6 (BACK)

-- Joias Boss +6
(300000001, @start_id := @start_id + 1, 6657, 1, 6, 'PAPERDOLL', 3),        -- Valakas' Necklace (NECK)
(300000001, @start_id := @start_id + 1, 6656, 1, 6, 'PAPERDOLL', 1),        -- Antharas' Earring (REAR)
(300000001, @start_id := @start_id + 1, 6659, 1, 6, 'PAPERDOLL', 2),        -- Zaken's Earring (LEAR)
(300000001, @start_id := @start_id + 1, 6658, 1, 6, 'PAPERDOLL', 4),       -- Ring of Baium (RFINGER)
(300000001, @start_id := @start_id + 1, 6660, 1, 6, 'PAPERDOLL', 5);       -- Ring of Queen Ant (LFINGER)

-- Elementals for TitanTester Gear
INSERT INTO `item_elementals` (`itemId`, `elemType`, `elemValue`) VALUES
(@t_weap, 1, 300), (@t_chest, 1, 120), (@t_legs, 1, 120), (@t_head, 1, 120), (@t_gloves, 1, 120), (@t_feet, 1, 120);

-- ===========================================================================
-- 8. FAKE SHOPS SETUP (Gludio Dwarves)
-- ===========================================================================
INSERT IGNORE INTO `accounts` (`login`, `password`, `accessLevel`) VALUES
('gimli_shop', 'botpass123', 0), ('thorin_shop', 'botpass123', 0), ('durin_shop', 'botpass123', 0), ('balin_shop', 'botpass123', 0), ('dwalin_shop', 'botpass123', 0),
('fili_shop', 'botpass123', 0), ('kili_shop', 'botpass123', 0), ('oin_shop', 'botpass123', 0), ('gloin_shop', 'botpass123', 0), ('bifur_shop', 'botpass123', 0),
('bofur_shop', 'botpass123', 0), ('bombur_shop', 'botpass123', 0), ('nori_shop', 'botpass123', 0), ('ori_shop', 'botpass123', 0), ('dori_shop', 'botpass123', 0);

REPLACE INTO `characters` (`account_name`, `charId`, `char_name`, `level`, `maxHp`, `curHp`, `maxMp`, `curMp`, `face`, `hairStyle`, `hairColor`, `sex`, `heading`, `x`, `y`, `z`, `race`, `classid`, `base_class`, `title`, `accesslevel`, `online`) VALUES
('gimli_shop', 200000001, 'Gimli', 40, 1500, 1500, 500, 500, 0, 0, 0, 0, 16384, -14228, 123445, -3115, 4, 53, 53, 'Mats Top D', 0, 0),
('thorin_shop', 200000002, 'Thorin', 52, 2200, 2200, 700, 700, 1, 1, 1, 0, 32768, -14180, 123480, -3115, 4, 56, 56, 'Refinados Top D', 0, 0),
('durin_shop', 200000003, 'Durin', 40, 1500, 1500, 500, 500, 2, 2, 2, 0, 49152, -14130, 123520, -3115, 4, 53, 53, 'Metais Raros Top D', 0, 0),
('balin_shop', 200000004, 'Balin', 35, 1200, 1200, 400, 400, 0, 1, 0, 0, 0, -14260, 123550, -3115, 4, 54, 54, 'Enchants Top D', 0, 0),
('dwalin_shop', 200000005, 'Dwalin', 35, 1200, 1200, 400, 400, 1, 2, 1, 0, 16384, -14310, 123500, -3115, 4, 55, 55, 'Shots Top D', 0, 0),
('fili_shop', 200000006, 'Fili', 40, 1800, 1800, 450, 450, 0, 0, 0, 0, 32768, -14350, 123445, -3115, 0, 9, 9, 'Potions Top D', 0, 0),
('kili_shop', 200000007, 'Kili', 35, 1100, 1100, 500, 500, 1, 0, 1, 0, 49152, -14228, 123380, -3115, 1, 22, 22, 'Bows & Arrows Top D', 0, 0),
('oin_shop', 200000008, 'Oin', 35, 800, 800, 1200, 1200, 2, 1, 0, 0, 0, -14180, 123340, -3115, 0, 11, 11, 'Magic Set Top D', 0, 0),
('gloin_shop', 200000009, 'Gloin', 35, 1600, 1600, 400, 400, 0, 2, 2, 0, 16384, -14130, 123380, -3115, 3, 45, 45, 'Heavy Set Top D', 0, 0),
('bifur_shop', 200000010, 'Bifur', 35, 1200, 1200, 450, 450, 1, 0, 0, 0, 32768, -14260, 123340, -3115, 2, 32, 32, 'Light Set Top D', 0, 0),
('bofur_shop', 200000011, 'Bofur', 35, 1200, 1200, 400, 400, 2, 1, 1, 0, 49152, -14080, 123445, -3115, 4, 54, 54, 'Buy Insumos Top D', 0, 0),
('bombur_shop', 200000012, 'Bombur', 35, 1200, 1200, 400, 400, 0, 2, 0, 0, 0, -14080, 123500, -3115, 4, 54, 54, 'Buy Mats Raros Top D', 0, 0),
('nori_shop', 200000013, 'Nori', 52, 2200, 2200, 700, 700, 1, 0, 2, 0, 16384, -14350, 123550, -3115, 4, 56, 56, 'Craft Shots Top D', 0, 0),
('ori_shop', 200000014, 'Ori', 52, 2200, 2200, 700, 700, 2, 1, 0, 0, 32768, -14380, 123500, -3115, 4, 56, 56, 'Craft Mats Top D', 0, 0),
('dori_shop', 200000015, 'Dori', 52, 2200, 2200, 700, 700, 0, 2, 1, 0, 49152, -14380, 123445, -3115, 4, 56, 56, 'Craft Equip Top D', 0, 0);

COMMIT;

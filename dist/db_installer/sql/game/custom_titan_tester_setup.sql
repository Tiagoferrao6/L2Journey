-- TitanTester Character, Subclasses, Skills, Hero, Clan, Custom Gear, Tattoos, Jewels & Supplies Setup

START TRANSACTION;

-- 1. Create/Update Character TitanTester (charId = 300000001)
REPLACE INTO `characters` (
  `account_name`, `charId`, `char_name`, `level`, `maxHp`, `curHp`, `maxCp`, `curCp`, `maxMp`, `curMp`,
  `face`, `hairStyle`, `hairColor`, `sex`, `heading`, `x`, `y`, `z`, `exp`, `sp`, `karma`, `fame`,
  `pvpkills`, `pkkills`, `clanid`, `race`, `classid`, `base_class`, `title`, `title_color`, `accesslevel`,
  `online`, `onlinetime`, `char_slot`, `newbie`, `clan_privs`, `nobless`, `createDate`
) VALUES (
  'tester', 300000001, 'TitanTester', 85, 15000, 15000, 10000, 10000, 5000, 5000,
  0, 0, 0, 0, 0, 83400, 147940, -3400, 6299999999, 2000000000, 0, 50000,
  100, 0, 100000, 3, 113, 113, 'Titan Hero', 15525282, 0,
  0, 36000, 1, 0, 16777215, 1, CURRENT_DATE()
);

-- 2. Subclasses Setup (DreadNought, Spectral Master, Ghost Hunter)
DELETE FROM `character_subclasses` WHERE `charId` = 300000001;
INSERT INTO `character_subclasses` (`charId`, `class_id`, `exp`, `sp`, `level`, `class_index`) VALUES
(300000001, 89, 6299999999, 2000000000, 85, 1),   -- Warrior: DreadNought
(300000001, 111, 6299999999, 2000000000, 85, 2),  -- Summoner: Spectral Master
(300000001, 108, 6299999999, 2000000000, 85, 3);  -- Assassin: Ghost Hunter

-- 3. Hero Table Registration & Hero Skills
REPLACE INTO `heroes` (`charId`, `class_id`, `count`, `played`, `claimed`, `message`) VALUES
(300000001, 113, 1, 1, 'true', 'Titan Hero Tester');

-- Hero Skills into character_skills
REPLACE INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES
(300000001, 395, 1, 0),
(300000001, 396, 1, 0),
(300000001, 1374, 1, 0),
(300000001, 1375, 1, 0),
(300000001, 1376, 1, 0);

-- 4. Clan Data & Clan Skills Provisioning
INSERT IGNORE INTO `clan_data` (`clan_id`, `clan_name`, `clan_level`, `reputation_score`, `leader_id`) VALUES
(100000, 'TesterClan', 11, 100000, 300000001);

DELETE FROM `clan_skills` WHERE `clan_id` = 100000;
INSERT INTO `clan_skills` (`clan_id`, `skill_id`, `skill_level`, `skill_name`, `sub_pledge_id`) VALUES
(100000, 370, 1, 'Clan Imperium', -2),
(100000, 371, 1, 'Clan Life Stone', -2),
(100000, 372, 2, 'Clan Life Stone', -2),
(100000, 373, 3, 'Clan Life Stone', -2),
(100000, 374, 4, 'Clan Life Stone', -2),
(100000, 375, 5, 'Clan Life Stone', -2),
(100000, 376, 6, 'Clan Life Stone', -2),
(100000, 377, 7, 'Clan Life Stone', -2),
(100000, 378, 8, 'Clan Life Stone', -2),
(100000, 379, 9, 'Clan Life Stone', -2),
(100000, 380, 10, 'Clan Life Stone', -2),
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

-- 5. Main Class Titan Skills (+30/+15 Max Enchants) & Subclass Cumulative Skills
-- Remove existing skills for this character to ensure clean setup
DELETE FROM `character_skills` WHERE `charId` = 300000001 AND `skill_id` NOT IN (395,396,1374,1375,1376);

INSERT INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES
-- Titan Main Class Buffs & Actives (+30 Enchants where applicable)
(300000001, 176, 3, 0),
(300000001, 139, 3, 0),
(300000001, 420, 3, 0),
(300000001, 121, 2, 0),
(300000001, 127, 6, 0),
(300000001, 190, 37, 0),
(300000001, 348, 37, 0),
(300000001, 362, 1, 0),
(300000001, 347, 1, 0),
(300000001, 440, 1, 0),
(300000001, 536, 1, 0),
(300000001, 777, 1, 0),
(300000001, 995, 1, 0),
(300000001, 758, 1, 0),
(300000001, 767, 1, 0),
-- Titan Passives (+30 Enchants where applicable)
(300000001, 211, 45, 0),
(300000001, 231, 52, 0),
(300000001, 258, 45, 0),
(300000001, 328, 1, 0),
(300000001, 329, 1, 0),
(300000001, 335, 1, 0),
(300000001, 339, 1, 0),
(300000001, 430, 1, 0),
-- Subclass Cumulative Skills (DreadNought, Spectral Master, Ghost Hunter)
(300000001, 361, 1, 0),
(300000001, 360, 1, 0),
(300000001, 438, 1, 0),
(300000001, 319, 37, 0),
(300000001, 1126, 1, 0),
(300000001, 1127, 1, 0),
(300000001, 263, 37, 0),
(300000001, 409, 1, 0),
(300000001, 358, 1, 0),
(300000001, 410, 1, 0);

-- 6. Items Population (Custom Armors, Custom Weapons, Tattoos, Boss Jewels & Supplies)
DELETE FROM `items` WHERE `owner_id` = 300000001;

-- Set start object_id for items safely
SET @start_id = 900000000;

-- Adena
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000001, @start_id := @start_id + 1, 57, 1000000000, 0, 'INVENTORY', 0);

-- Consumables & Supplies
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000001, @start_id := @start_id + 1, 5592, 500, 0, 'INVENTORY', 0),    -- Greater CP Potion
(300000001, @start_id := @start_id + 1, 1539, 500, 0, 'INVENTORY', 0),    -- Greater Healing Potion
(300000001, @start_id := @start_id + 1, 728, 500, 0, 'INVENTORY', 0),     -- Mana Potion
(300000001, @start_id := @start_id + 1, 1538, 100, 0, 'INVENTORY', 0),    -- Blessed Scroll of Escape
(300000001, @start_id := @start_id + 1, 6393, 100, 0, 'INVENTORY', 0),    -- Blessed Scroll of Resurrection
(300000001, @start_id := @start_id + 1, 1467, 100000, 0, 'INVENTORY', 0), -- Soulshot S-Grade
(300000001, @start_id := @start_id + 1, 3952, 100000, 0, 'INVENTORY', 0); -- Blessed Spiritshot S-Grade

-- Materials & Enchants
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000001, @start_id := @start_id + 1, 9625, 500, 0, 'INVENTORY', 0),   -- Giant's Code - Mastery
(300000001, @start_id := @start_id + 1, 6622, 500, 0, 'INVENTORY', 0),   -- Giant's Code - Normal
(300000001, @start_id := @start_id + 1, 12753, 100, 0, 'INVENTORY', 0),  -- Top-Grade Life Stone 84/85
(300000001, @start_id := @start_id + 1, 9546, 300, 0, 'INVENTORY', 0),   -- Fire Stone
(300000001, @start_id := @start_id + 1, 9547, 300, 0, 'INVENTORY', 0),   -- Water Stone
(300000001, @start_id := @start_id + 1, 9548, 300, 0, 'INVENTORY', 0),   -- Wind Stone
(300000001, @start_id := @start_id + 1, 9549, 300, 0, 'INVENTORY', 0),   -- Earth Stone
(300000001, @start_id := @start_id + 1, 9550, 300, 0, 'INVENTORY', 0),   -- Holy Stone
(300000001, @start_id := @start_id + 1, 9551, 300, 0, 'INVENTORY', 0);   -- Dark Stone

-- Royal Custom Armor Sets (Heavy, Light, Robe, Cloak, Shield)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000001, @start_id := @start_id + 1, 99201, 1, 0, 'INVENTORY', 0), -- Royal Breastplate - Weapon Master
(300000001, @start_id := @start_id + 1, 99204, 1, 0, 'INVENTORY', 0), -- Royal Gaiters
(300000001, @start_id := @start_id + 1, 99205, 1, 0, 'INVENTORY', 0), -- Royal Helmet
(300000001, @start_id := @start_id + 1, 99206, 1, 0, 'INVENTORY', 0), -- Royal Gauntlet - Heavy
(300000001, @start_id := @start_id + 1, 99207, 1, 0, 'INVENTORY', 0), -- Royal Boots - Heavy
(300000001, @start_id := @start_id + 1, 99208, 1, 0, 'INVENTORY', 0), -- Royal Shield
(300000001, @start_id := @start_id + 1, 99224, 1, 0, 'INVENTORY', 0), -- Royal Cloak
(300000001, @start_id := @start_id + 1, 99210, 1, 0, 'INVENTORY', 0), -- Royal Leather Armor - Bow Master
(300000001, @start_id := @start_id + 1, 99211, 1, 0, 'INVENTORY', 0), -- Royal Leather Leggings
(300000001, @start_id := @start_id + 1, 99212, 1, 0, 'INVENTORY', 0), -- Royal Leather Helmet
(300000001, @start_id := @start_id + 1, 99213, 1, 0, 'INVENTORY', 0), -- Royal Leather Gloves
(300000001, @start_id := @start_id + 1, 99214, 1, 0, 'INVENTORY', 0), -- Royal Leather Boots
(300000001, @start_id := @start_id + 1, 99218, 1, 0, 'INVENTORY', 0), -- Royal Tunic - Wizard
(300000001, @start_id := @start_id + 1, 99219, 1, 0, 'INVENTORY', 0), -- Royal Stockings
(300000001, @start_id := @start_id + 1, 99220, 1, 0, 'INVENTORY', 0), -- Royal Circlet
(300000001, @start_id := @start_id + 1, 99221, 1, 0, 'INVENTORY', 0), -- Royal Gloves - Robe
(300000001, @start_id := @start_id + 1, 99222, 1, 0, 'INVENTORY', 0), -- Royal Shoes - Robe
(300000001, @start_id := @start_id + 1, 99223, 1, 0, 'INVENTORY', 0); -- Royal Sigil

-- All 16 Custom Royal Dynasty Weapons (99300 to 99315)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000001, @start_id := @start_id + 1, 99300, 1, 0, 'INVENTORY', 0), -- Blade (1H Sword)
(300000001, @start_id := @start_id + 1, 99301, 1, 0, 'INVENTORY', 0), -- Guardian (2H Sword)
(300000001, @start_id := @start_id + 1, 99302, 1, 0, 'INVENTORY', 0), -- Crusher (2H Hammer)
(300000001, @start_id := @start_id + 1, 99303, 1, 0, 'INVENTORY', 0), -- Cudgel (1H Hammer)
(300000001, @start_id := @start_id + 1, 99304, 1, 0, 'INVENTORY', 0), -- Baghnakh (Fists)
(300000001, @start_id := @start_id + 1, 99305, 1, 0, 'INVENTORY', 0), -- Knife (Dagger)
(300000001, @start_id := @start_id + 1, 99306, 1, 0, 'INVENTORY', 0), -- Bow
(300000001, @start_id := @start_id + 1, 99307, 1, 0, 'INVENTORY', 0), -- Crossbow
(300000001, @start_id := @start_id + 1, 99308, 1, 0, 'INVENTORY', 0), -- Halberd (Polearm)
(300000001, @start_id := @start_id + 1, 99309, 1, 0, 'INVENTORY', 0), -- Dual Blade
(300000001, @start_id := @start_id + 1, 99310, 1, 0, 'INVENTORY', 0), -- Dual Daggers
(300000001, @start_id := @start_id + 1, 99311, 1, 0, 'INVENTORY', 0), -- Phantom Staff
(300000001, @start_id := @start_id + 1, 99312, 1, 0, 'INVENTORY', 0), -- Mace
(300000001, @start_id := @start_id + 1, 99313, 1, 0, 'INVENTORY', 0), -- Staff
(300000001, @start_id := @start_id + 1, 99314, 1, 0, 'INVENTORY', 0), -- Rapier
(300000001, @start_id := @start_id + 1, 99315, 1, 0, 'INVENTORY', 0); -- Ancient Sword

-- All 14 Level 6 Tattoos (Right & Left Slots)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000001, @start_id := @start_id + 1, 41006, 1, 0, 'INVENTORY', 0), -- Tattoo Ogre Lv6 (Right)
(300000001, @start_id := @start_id + 1, 41012, 1, 0, 'INVENTORY', 0), -- Tattoo Monk Lv6 (Right)
(300000001, @start_id := @start_id + 1, 41018, 1, 0, 'INVENTORY', 0), -- Tattoo Assassin Lv6 (Right)
(300000001, @start_id := @start_id + 1, 41024, 1, 0, 'INVENTORY', 0), -- Tattoo Blood Lv6 (Right)
(300000001, @start_id := @start_id + 1, 41030, 1, 0, 'INVENTORY', 0), -- Tattoo Soul Lv6 (Right)
(300000001, @start_id := @start_id + 1, 41036, 1, 0, 'INVENTORY', 0), -- Tattoo Flame Lv6 (Right)
(300000001, @start_id := @start_id + 1, 41042, 1, 0, 'INVENTORY', 0), -- Tattoo Absolute Lv6 (Right)
(300000001, @start_id := @start_id + 1, 41048, 1, 0, 'INVENTORY', 0), -- Tattoo Ogre Lv6 (Left)
(300000001, @start_id := @start_id + 1, 41054, 1, 0, 'INVENTORY', 0), -- Tattoo Monk Lv6 (Left)
(300000001, @start_id := @start_id + 1, 41060, 1, 0, 'INVENTORY', 0), -- Tattoo Assassin Lv6 (Left)
(300000001, @start_id := @start_id + 1, 41066, 1, 0, 'INVENTORY', 0), -- Tattoo Blood Lv6 (Left)
(300000001, @start_id := @start_id + 1, 41072, 1, 0, 'INVENTORY', 0), -- Tattoo Soul Lv6 (Left)
(300000001, @start_id := @start_id + 1, 41078, 1, 0, 'INVENTORY', 0), -- Tattoo Flame Lv6 (Left)
(300000001, @start_id := @start_id + 1, 41084, 1, 0, 'INVENTORY', 0); -- Tattoo Absolute Lv6 (Left)

-- Epic Boss Jewels
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000001, @start_id := @start_id + 1, 6658, 1, 0, 'INVENTORY', 0),  -- Ring of Baium
(300000001, @start_id := @start_id + 1, 6659, 1, 0, 'INVENTORY', 0),  -- Zaken's Earring
(300000001, @start_id := @start_id + 1, 6656, 1, 0, 'INVENTORY', 0),  -- Antharas' Earring
(300000001, @start_id := @start_id + 1, 6657, 1, 0, 'INVENTORY', 0),  -- Valakas' Necklace
(300000001, @start_id := @start_id + 1, 6660, 1, 0, 'INVENTORY', 0),  -- Ring of Queen Ant
(300000001, @start_id := @start_id + 1, 10314, 1, 0, 'INVENTORY', 0); -- Beleth's Ring

-- Boss Access & Quest Items
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000001, @start_id := @start_id + 1, 4295, 10, 0, 'INVENTORY', 0),  -- Blooded Fabric (Baium)
(300000001, @start_id := @start_id + 1, 3865, 10, 0, 'INVENTORY', 0),  -- Floating Stone (Antharas)
(300000001, @start_id := @start_id + 1, 7267, 10, 0, 'INVENTORY', 0),  -- Portal Stone (Valakas)
(300000001, @start_id := @start_id + 1, 8073, 10, 0, 'INVENTORY', 0),  -- Frintezza's Scroll
(300000001, @start_id := @start_id + 1, 7694, 1, 0, 'INVENTORY', 0);   -- Noblesse Tiara

COMMIT;

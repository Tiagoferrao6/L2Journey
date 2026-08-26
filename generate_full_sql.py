import re

with open('silver_skills.txt', 'r') as f:
    silver_skills = f.read().strip().rstrip(',')

with open('titan_skills.txt', 'r') as f:
    titan_skills = f.read().strip().rstrip(',')

items_sql = """
-- ===========================================================================
-- 7. ITEMS POPULATION (Calibrado para peso < 100%)
-- ===========================================================================
DELETE FROM `items` WHERE `owner_id` IN (300000000, 300000001);
DELETE FROM `item_elementals` WHERE `itemId` IN (SELECT `object_id` FROM `items` WHERE `owner_id` IN (300000000, 300000001));

SET @start_id = 900000000;

-- 7.1 SilverTester Items (300000000)
-- Consumables & Materials (INVENTORY)
INSERT INTO `items` (`owner_id`, `object_id`, `item_id`, `count`, `enchant_level`, `loc`, `loc_data`) VALUES
(300000000, @start_id := @start_id + 1, 57, 1000000000, 0, 'INVENTORY', 0),    -- 1 Bi Adena
(300000000, @start_id := @start_id + 1, 4356, 500, 0, 'INVENTORY', 0),     -- 500 Raid Coins
(300000000, @start_id := @start_id + 1, 1467, 10000, 0, 'INVENTORY', 0),     -- Soulshot S
(300000000, @start_id := @start_id + 1, 3952, 10000, 0, 'INVENTORY', 0),     -- Blessed Spiritshot S
(300000000, @start_id := @start_id + 1, 1341, 20000, 0, 'INVENTORY', 0),     -- Bone Arrow S / Shining Arrow
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

-- Tattoos
(300000000, @start_id := @start_id + 1, 41006, 1, 0, 'INVENTORY', 0), -- Ogre Lv 6
(300000000, @start_id := @start_id + 1, 41012, 1, 0, 'INVENTORY', 0), -- Monk Lv 6
(300000000, @start_id := @start_id + 1, 41018, 1, 0, 'INVENTORY', 0), -- Assassin Lv 6
(300000000, @start_id := @start_id + 1, 41024, 1, 0, 'INVENTORY', 0), -- Blood Lv 6

-- SilverTester EQUIPPED Gear (PAPERDOLL)
(300000000, @r_bow := @start_id + 1, 99306, 1, 6, 'PAPERDOLL', 5),       -- Royal Dynasty Bow +6 (RHAND)
(300000000, @r_chest := @start_id + 2, 99210, 1, 6, 'PAPERDOLL', 6),       -- Royal Light Armor +6 (CHEST)
(300000000, @r_legs := @start_id + 3, 99211, 1, 6, 'PAPERDOLL', 11),      -- Royal Light Leggings +6 (LEGS)
(300000000, @r_head := @start_id + 4, 99212, 1, 6, 'PAPERDOLL', 1),       -- Royal Leather Helmet +6 (HEAD)
(300000000, @r_gloves := @start_id + 5, 99213, 1, 6, 'PAPERDOLL', 10),      -- Royal Leather Gloves +6 (GLOVES)
(300000000, @r_feet := @start_id + 6, 99214, 1, 6, 'PAPERDOLL', 12),      -- Royal Leather Boots +6 (FEET)
(300000000, @start_id := @start_id + 7, 99224, 1, 6, 'PAPERDOLL', 23),      -- Royal Cloak +6 (CLOAK)

-- Joias Boss +6
(300000000, @start_id := @start_id + 1, 6657, 1, 6, 'PAPERDOLL', 4),        -- Valakas' Necklace (NECK)
(300000000, @start_id := @start_id + 1, 6656, 1, 6, 'PAPERDOLL', 8),        -- Antharas' Earring (REAR)
(300000000, @start_id := @start_id + 1, 6659, 1, 6, 'PAPERDOLL', 9),        -- Zaken's Earring (LEAR)
(300000000, @start_id := @start_id + 1, 6658, 1, 6, 'PAPERDOLL', 13),       -- Ring of Baium (RFINGER)
(300000000, @start_id := @start_id + 1, 6660, 1, 6, 'PAPERDOLL', 14),       -- Ring of Queen Ant (LFINGER)

-- All Royal Weapons
(300000000, @rw1 := @start_id + 1, 99300, 1, 6, 'INVENTORY', 0), (300000000, @rw2 := @start_id + 2, 99301, 1, 6, 'INVENTORY', 0),
(300000000, @rw3 := @start_id + 3, 99302, 1, 6, 'INVENTORY', 0), (300000000, @rw4 := @start_id + 4, 99303, 1, 6, 'INVENTORY', 0),
(300000000, @rw5 := @start_id + 5, 99304, 1, 6, 'INVENTORY', 0), (300000000, @rw6 := @start_id + 6, 99305, 1, 6, 'INVENTORY', 0),
(300000000, @rw7 := @start_id + 7, 99307, 1, 6, 'INVENTORY', 0), (300000000, @rw8 := @start_id + 8, 99308, 1, 6, 'INVENTORY', 0),
(300000000, @rw9 := @start_id + 9, 99309, 1, 6, 'INVENTORY', 0), (300000000, @rw10 := @start_id + 10, 99310, 1, 6, 'INVENTORY', 0),
(300000000, @rw11 := @start_id + 11, 99311, 1, 6, 'INVENTORY', 0), (300000000, @rw12 := @start_id + 12, 99312, 1, 6, 'INVENTORY', 0),
(300000000, @rw13 := @start_id + 13, 99313, 1, 6, 'INVENTORY', 0), (300000000, @rw14 := @start_id + 14, 99314, 1, 6, 'INVENTORY', 0),
(300000000, @start_id := @start_id + 15, 99315, 1, 6, 'INVENTORY', 0);

-- Elementals for SilverTester Gear
INSERT INTO `item_elementals` (`itemId`, `elemType`, `elemValue`) VALUES
(@r_bow, 0, 300), (@r_chest, 0, 120), (@r_legs, 0, 120), (@r_head, 0, 120), (@r_gloves, 0, 120), (@r_feet, 0, 120),
(@rw1, 0, 300), (@rw2, 0, 300), (@rw3, 0, 300), (@rw4, 0, 300), (@rw5, 0, 300), (@rw6, 0, 300), (@rw7, 0, 300),
(@rw8, 0, 300), (@rw9, 0, 300), (@rw10, 0, 300), (@rw11, 0, 300), (@rw12, 0, 300), (@rw13, 0, 300), (@rw14, 0, 300);

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
(300000001, @t_weap := @start_id + 1, 99301, 1, 6, 'PAPERDOLL', 5),       -- Royal Dynasty Guardian +6 (RHAND)
(300000001, @t_chest := @start_id + 2, 99201, 1, 6, 'PAPERDOLL', 6),       -- Royal Breastplate - Weapon Master +6 (CHEST)
(300000001, @t_legs := @start_id + 3, 99204, 1, 6, 'PAPERDOLL', 11),       -- Royal Gaiters +6 (LEGS)
(300000001, @t_head := @start_id + 4, 99205, 1, 6, 'PAPERDOLL', 1),        -- Royal Helmet +6 (HEAD)
(300000001, @t_gloves := @start_id + 5, 99206, 1, 6, 'PAPERDOLL', 10),       -- Royal Gauntlets +6 (GLOVES)
(300000001, @t_feet := @start_id + 6, 99207, 1, 6, 'PAPERDOLL', 12),       -- Royal Boots +6 (FEET)
(300000001, @start_id := @start_id + 7, 99224, 1, 6, 'PAPERDOLL', 23),       -- Royal Cloak +6 (CLOAK)

-- Joias Boss +6
(300000001, @start_id := @start_id + 1, 6657, 1, 6, 'PAPERDOLL', 4),        -- Valakas' Necklace (NECK)
(300000001, @start_id := @start_id + 1, 6656, 1, 6, 'PAPERDOLL', 8),        -- Antharas' Earring (REAR)
(300000001, @start_id := @start_id + 1, 6659, 1, 6, 'PAPERDOLL', 9),        -- Zaken's Earring (LEAR)
(300000001, @start_id := @start_id + 1, 6658, 1, 6, 'PAPERDOLL', 13),       -- Ring of Baium (RFINGER)
(300000001, @start_id := @start_id + 1, 6660, 1, 6, 'PAPERDOLL', 14),       -- Ring of Queen Ant (LFINGER)

-- All Royal Weapons
(300000001, @tw1 := @start_id + 1, 99300, 1, 6, 'INVENTORY', 0), (300000001, @tw2 := @start_id + 2, 99302, 1, 6, 'INVENTORY', 0),
(300000001, @tw3 := @start_id + 3, 99303, 1, 6, 'INVENTORY', 0), (300000001, @tw4 := @start_id + 4, 99304, 1, 6, 'INVENTORY', 0),
(300000001, @tw5 := @start_id + 5, 99305, 1, 6, 'INVENTORY', 0), (300000001, @tw6 := @start_id + 6, 99306, 1, 6, 'INVENTORY', 0),
(300000001, @tw7 := @start_id + 7, 99307, 1, 6, 'INVENTORY', 0), (300000001, @tw8 := @start_id + 8, 99308, 1, 6, 'INVENTORY', 0),
(300000001, @tw9 := @start_id + 9, 99309, 1, 6, 'INVENTORY', 0), (300000001, @tw10 := @start_id + 10, 99310, 1, 6, 'INVENTORY', 0),
(300000001, @tw11 := @start_id + 11, 99311, 1, 6, 'INVENTORY', 0), (300000001, @tw12 := @start_id + 12, 99312, 1, 6, 'INVENTORY', 0),
(300000001, @tw13 := @start_id + 13, 99313, 1, 6, 'INVENTORY', 0), (300000001, @tw14 := @start_id + 14, 99314, 1, 6, 'INVENTORY', 0),
(300000001, @start_id := @start_id + 15, 99315, 1, 6, 'INVENTORY', 0);

-- Elementals for TitanTester Gear
INSERT INTO `item_elementals` (`itemId`, `elemType`, `elemValue`) VALUES
(@t_weap, 1, 300), (@t_chest, 1, 120), (@t_legs, 1, 120), (@t_head, 1, 120), (@t_gloves, 1, 120), (@t_feet, 1, 120),
(@tw1, 1, 300), (@tw2, 1, 300), (@tw3, 1, 300), (@tw4, 1, 300), (@tw5, 1, 300), (@tw6, 1, 300), (@tw7, 1, 300),
(@tw8, 1, 300), (@tw9, 1, 300), (@tw10, 1, 300), (@tw11, 1, 300), (@tw12, 1, 300), (@tw13, 1, 300), (@tw14, 1, 300);

COMMIT;
"""

with open('dist/db_installer/sql/game/z_custom_test_characters_setup.sql', 'r') as f:
    original = f.read()

# Replace the part from "6. CHARACTER SKILLS PROVISIONING" to the end
idx1 = original.find("-- 6. CHARACTER SKILLS PROVISIONING")
original = original[:idx1]

new_sql = original + """-- 6. CHARACTER SKILLS PROVISIONING
-- ===========================================================================
DELETE FROM `character_skills` WHERE `charId` IN (300000000, 300000001);

-- 6.1 SilverTester Skills (Moonlight Sentinel + Sword Muse Cumulative Songs)
INSERT IGNORE INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES
""" + silver_skills + """;

-- 6.2 TitanTester Skills (Titan Main Class + DreadNought Cumulative Skills)
INSERT IGNORE INTO `character_skills` (`charId`, `skill_id`, `skill_level`, `class_index`) VALUES
""" + titan_skills + """;
""" + items_sql

with open('dist/db_installer/sql/game/z_custom_test_characters_setup.sql', 'w') as f:
    f.write(new_sql)

print("SQL script rewritten successfully.")

-- ---------------------------------------------------------------------------
-- 15 Contas e Personagens Fixos para FakeShops de Gludio (Top D-Grade)
-- ---------------------------------------------------------------------------

-- Insercao de Contas
INSERT IGNORE INTO `accounts` (`login`, `password`, `accessLevel`) VALUES
('gimli_shop', 'botpass123', 0),
('thorin_shop', 'botpass123', 0),
('durin_shop', 'botpass123', 0),
('balin_shop', 'botpass123', 0),
('dwalin_shop', 'botpass123', 0),
('fili_shop', 'botpass123', 0),
('kili_shop', 'botpass123', 0),
('oin_shop', 'botpass123', 0),
('gloin_shop', 'botpass123', 0),
('bifur_shop', 'botpass123', 0),
('bofur_shop', 'botpass123', 0),
('bombur_shop', 'botpass123', 0),
('nori_shop', 'botpass123', 0),
('ori_shop', 'botpass123', 0),
('dori_shop', 'botpass123', 0);

-- Insercao de Personagens com IDs Fixos e Coordenadas em Gludio
INSERT IGNORE INTO `characters` (`account_name`, `charId`, `char_name`, `level`, `maxHp`, `curHp`, `maxMp`, `curMp`, `face`, `hairStyle`, `hairColor`, `sex`, `heading`, `x`, `y`, `z`, `race`, `classid`, `base_class`, `title`, `accesslevel`, `online`) VALUES
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

-- ---------------------------------------------------------------------------
-- Ecossistema Global de FakeShops - Contas e Personagens (130 Lojas)
-- ---------------------------------------------------------------------------

-- Inserção de Contas em Lote
INSERT IGNORE INTO `accounts` (`login`, `password`, `accessLevel`) VALUES
-- Gludio (15)
('gimli_shop', 'botpass123', 0), ('thorin_shop', 'botpass123', 0), ('durin_shop', 'botpass123', 0), ('balin_shop', 'botpass123', 0), ('dwalin_shop', 'botpass123', 0),
('fili_shop', 'botpass123', 0), ('kili_shop', 'botpass123', 0), ('oin_shop', 'botpass123', 0), ('gloin_shop', 'botpass123', 0), ('bifur_shop', 'botpass123', 0),
('bofur_shop', 'botpass123', 0), ('bombur_shop', 'botpass123', 0), ('nori_shop', 'botpass123', 0), ('ori_shop', 'botpass123', 0), ('dori_shop', 'botpass123', 0),

-- Dion (15)
('dion_trader1', 'botpass123', 0), ('dion_trader2', 'botpass123', 0), ('dion_trader3', 'botpass123', 0), ('dion_trader4', 'botpass123', 0), ('dion_trader5', 'botpass123', 0),
('dion_trader6', 'botpass123', 0), ('dion_trader7', 'botpass123', 0), ('dion_trader8', 'botpass123', 0), ('dion_trader9', 'botpass123', 0), ('dion_trader10', 'botpass123', 0),
('dion_trader11', 'botpass123', 0), ('dion_trader12', 'botpass123', 0), ('dion_trader13', 'botpass123', 0), ('dion_trader14', 'botpass123', 0), ('dion_trader15', 'botpass123', 0),

-- Giran (50)
('giran_mat1', 'botpass123', 0), ('giran_mat2', 'botpass123', 0), ('giran_mat3', 'botpass123', 0), ('giran_mat4', 'botpass123', 0), ('giran_mat5', 'botpass123', 0),
('giran_mat6', 'botpass123', 0), ('giran_mat7', 'botpass123', 0), ('giran_mat8', 'botpass123', 0), ('giran_mat9', 'botpass123', 0), ('giran_mat10', 'botpass123', 0),
('giran_sup1', 'botpass123', 0), ('giran_sup2', 'botpass123', 0), ('giran_sup3', 'botpass123', 0), ('giran_sup4', 'botpass123', 0), ('giran_sup5', 'botpass123', 0),
('giran_sup6', 'botpass123', 0), ('giran_sup7', 'botpass123', 0), ('giran_sup8', 'botpass123', 0), ('giran_sup9', 'botpass123', 0), ('giran_sup10', 'botpass123', 0),
('giran_eq1', 'botpass123', 0), ('giran_eq2', 'botpass123', 0), ('giran_eq3', 'botpass123', 0), ('giran_eq4', 'botpass123', 0), ('giran_eq5', 'botpass123', 0),
('giran_eq6', 'botpass123', 0), ('giran_eq7', 'botpass123', 0), ('giran_eq8', 'botpass123', 0), ('giran_eq9', 'botpass123', 0), ('giran_eq10', 'botpass123', 0),
('giran_eq11', 'botpass123', 0), ('giran_eq12', 'botpass123', 0), ('giran_eq13', 'botpass123', 0), ('giran_eq14', 'botpass123', 0), ('giran_eq15', 'botpass123', 0),
('giran_top1', 'botpass123', 0), ('giran_top2', 'botpass123', 0), ('giran_top3', 'botpass123', 0), ('giran_top4', 'botpass123', 0), ('giran_top5', 'botpass123', 0),
('giran_top6', 'botpass123', 0), ('giran_top7', 'botpass123', 0), ('giran_top8', 'botpass123', 0), ('giran_top9', 'botpass123', 0), ('giran_top10', 'botpass123', 0),
('giran_top11', 'botpass123', 0), ('giran_top12', 'botpass123', 0), ('giran_top13', 'botpass123', 0), ('giran_top14', 'botpass123', 0), ('giran_top15', 'botpass123', 0),

-- Aden (20)
('aden_e1', 'botpass123', 0), ('aden_e2', 'botpass123', 0), ('aden_e3', 'botpass123', 0), ('aden_e4', 'botpass123', 0), ('aden_e5', 'botpass123', 0),
('aden_e6', 'botpass123', 0), ('aden_e7', 'botpass123', 0), ('aden_e8', 'botpass123', 0), ('aden_e9', 'botpass123', 0), ('aden_e10', 'botpass123', 0),
('aden_e11', 'botpass123', 0), ('aden_e12', 'botpass123', 0), ('aden_e13', 'botpass123', 0), ('aden_e14', 'botpass123', 0), ('aden_e15', 'botpass123', 0),
('aden_e16', 'botpass123', 0), ('aden_e17', 'botpass123', 0), ('aden_e18', 'botpass123', 0), ('aden_e19', 'botpass123', 0), ('aden_e20', 'botpass123', 0),

-- Cidades Secundárias (30)
('oren_s1', 'botpass123', 0), ('oren_s2', 'botpass123', 0), ('oren_s3', 'botpass123', 0), ('oren_s4', 'botpass123', 0), ('oren_s5', 'botpass123', 0),
('hunters_s1', 'botpass123', 0), ('hunters_s2', 'botpass123', 0), ('hunters_s3', 'botpass123', 0), ('hunters_s4', 'botpass123', 0), ('hunters_s5', 'botpass123', 0),
('heine_s1', 'botpass123', 0), ('heine_s2', 'botpass123', 0), ('heine_s3', 'botpass123', 0), ('heine_s4', 'botpass123', 0), ('heine_s5', 'botpass123', 0),
('goddard_s1', 'botpass123', 0), ('goddard_s2', 'botpass123', 0), ('goddard_s3', 'botpass123', 0), ('goddard_s4', 'botpass123', 0), ('goddard_s5', 'botpass123', 0),
('rune_s1', 'botpass123', 0), ('rune_s2', 'botpass123', 0), ('rune_s3', 'botpass123', 0), ('rune_s4', 'botpass123', 0), ('rune_s5', 'botpass123', 0),
('schutt_s1', 'botpass123', 0), ('schutt_s2', 'botpass123', 0), ('schutt_s3', 'botpass123', 0), ('schutt_s4', 'botpass123', 0), ('schutt_s5', 'botpass123', 0);

-- Inserção de Personagens em Lote
INSERT IGNORE INTO `characters` (`account_name`, `charId`, `char_name`, `level`, `maxHp`, `curHp`, `maxMp`, `curMp`, `face`, `hairStyle`, `hairColor`, `sex`, `heading`, `x`, `y`, `z`, `race`, `classid`, `base_class`, `title`, `accesslevel`, `online`) VALUES
-- Gludio (15)
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
('dori_shop', 200000015, 'Dori', 52, 2200, 2200, 700, 700, 0, 2, 1, 0, 49152, -14380, 123445, -3115, 4, 56, 56, 'Craft Equip Top D', 0, 0),

-- Dion (15)
('dion_trader1', 200000016, 'DionTrader1', 45, 1800, 1800, 600, 600, 0, 0, 0, 0, 16384, 15632, 142876, -2705, 4, 53, 53, 'Mats Transicao D/C', 0, 0),
('dion_trader2', 200000017, 'DionTrader2', 45, 1800, 1800, 600, 600, 1, 1, 1, 0, 32768, 15680, 142910, -2705, 4, 55, 55, 'Shots & Cristais C', 0, 0),
('dion_trader3', 200000018, 'DionTrader3', 45, 1800, 1800, 600, 600, 2, 2, 2, 0, 49152, 15730, 142950, -2705, 4, 54, 54, 'Enchants C & Gems', 0, 0),
('dion_trader4', 200000019, 'DionTrader4', 45, 1800, 1800, 600, 600, 0, 1, 0, 0, 0, 15580, 142840, -2705, 0, 9, 9, 'Potions & Res/SoE', 0, 0),
('dion_trader5', 200000020, 'DionTrader5', 45, 2000, 2000, 500, 500, 1, 0, 0, 0, 16384, 15530, 142800, -2705, 0, 1, 1, 'Katana & C Swords', 0, 0),
('dion_trader6', 200000021, 'DionTrader6', 45, 1700, 1700, 550, 550, 0, 2, 1, 0, 32768, 15632, 142750, -2705, 2, 22, 22, 'Dark Elven Dagger C', 0, 0),
('dion_trader7', 200000022, 'DionTrader7', 45, 1600, 1600, 600, 600, 1, 1, 0, 0, 49152, 15680, 142710, -2705, 1, 22, 22, 'Akat Longbow C', 0, 0),
('dion_trader8', 200000023, 'DionTrader8', 45, 1200, 1200, 1400, 1400, 2, 0, 2, 0, 0, 15730, 142670, -2705, 0, 11, 11, 'Sages Staff C', 0, 0),
('dion_trader9', 200000024, 'DionTrader9', 45, 1750, 1750, 500, 500, 0, 0, 1, 0, 16384, 15780, 142800, -2705, 2, 32, 32, 'Plated Leather C', 0, 0),
('dion_trader10', 200000025, 'DionTrader10', 45, 1200, 1200, 1400, 1400, 1, 2, 0, 0, 32768, 15780, 142850, -2705, 0, 11, 11, 'Demons Tunic C', 0, 0),
('dion_trader11', 200000026, 'DionTrader11', 45, 2200, 2200, 600, 600, 0, 1, 2, 0, 49152, 15780, 142900, -2705, 3, 45, 45, 'Joias C-Grade', 0, 0),
('dion_trader12', 200000027, 'DionTrader12', 45, 1800, 1800, 600, 600, 2, 0, 0, 0, 0, 15530, 142950, -2705, 4, 54, 54, 'Compro Cristais D/C', 0, 0),
('dion_trader13', 200000028, 'DionTrader13', 45, 1800, 1800, 600, 600, 1, 1, 1, 0, 16384, 15480, 142900, -2705, 4, 54, 54, 'Compro Couro/Cokes', 0, 0),
('dion_trader14', 200000029, 'DionTrader14', 52, 2200, 2200, 700, 700, 0, 2, 2, 0, 32768, 15480, 142850, -2705, 4, 56, 56, 'Craft SS C & BSS C', 0, 0),
('dion_trader15', 200000030, 'DionTrader15', 52, 2200, 2200, 700, 700, 2, 0, 1, 0, 49152, 15480, 142800, -2705, 4, 56, 56, 'Craft Equip C', 0, 0),

-- Giran (50)
('giran_mat1', 200000031, 'GiranMat1', 55, 2500, 2500, 800, 800, 0, 0, 0, 0, 0, 83400, 147940, -3404, 4, 53, 53, 'Steel & Cokes', 0, 0),
('giran_mat2', 200000032, 'GiranMat2', 60, 2800, 2800, 900, 900, 1, 1, 1, 0, 16384, 83450, 147980, -3404, 4, 56, 56, 'Durable Braid', 0, 0),
('giran_mat3', 200000033, 'GiranMat3', 60, 2800, 2800, 900, 900, 2, 2, 2, 0, 32768, 83500, 148020, -3404, 4, 53, 53, 'Mithril & Oriharukon', 0, 0),
('giran_mat4', 200000034, 'GiranMat4', 60, 2800, 2800, 900, 900, 0, 1, 0, 0, 49152, 83550, 148060, -3404, 4, 54, 54, 'Enria & Asofe', 0, 0),
('giran_mat5', 200000035, 'GiranMat5', 65, 3000, 3000, 1000, 1000, 1, 0, 2, 0, 0, 83600, 148100, -3404, 4, 56, 56, 'Mold Lubricant', 0, 0),
('giran_mat6', 200000036, 'GiranMat6', 55, 2500, 2500, 800, 800, 2, 1, 0, 0, 16384, 83350, 147900, -3404, 4, 54, 54, 'Buy Steel & Iron', 0, 0),
('giran_mat7', 200000037, 'GiranMat7', 60, 2800, 2800, 900, 900, 0, 2, 1, 0, 32768, 83300, 147860, -3404, 4, 54, 54, 'Buy Enria & Asofe', 0, 0),
('giran_mat8', 200000038, 'GiranMat8', 65, 3000, 3000, 1000, 1000, 1, 0, 0, 0, 49152, 83250, 147820, -3404, 4, 56, 56, 'Craft Refinados', 0, 0),
('giran_mat9', 200000039, 'GiranMat9', 65, 3000, 3000, 1000, 1000, 2, 1, 1, 0, 0, 83200, 147780, -3404, 4, 56, 56, 'Craft Oriharukon', 0, 0),
('giran_mat10', 200000040, 'GiranMat10', 65, 3000, 3000, 1000, 1000, 0, 2, 2, 0, 16384, 83150, 147740, -3404, 4, 56, 56, 'Craft Mold Lubricant', 0, 0),

('giran_sup1', 200000041, 'GiranSup1', 60, 2800, 2800, 900, 900, 1, 0, 0, 0, 32768, 83650, 147940, -3404, 4, 54, 54, 'Enchants & Cristais B', 0, 0),
('giran_sup2', 200000042, 'GiranSup2', 65, 3000, 3000, 1000, 1000, 2, 1, 0, 0, 49152, 83700, 147980, -3404, 4, 54, 54, 'Enchants & Cristais A', 0, 0),
('giran_sup3', 200000043, 'GiranSup3', 70, 3200, 3200, 1100, 1100, 0, 2, 1, 0, 0, 83750, 148020, -3404, 4, 54, 54, 'Enchants & Cristais S', 0, 0),
('giran_sup4', 200000044, 'GiranSup4', 65, 3000, 3000, 1000, 1000, 1, 0, 2, 0, 16384, 83800, 148060, -3404, 4, 55, 55, 'Gemstones B/A/S', 0, 0),
('giran_sup5', 200000045, 'GiranSup5', 60, 2800, 2800, 900, 900, 2, 1, 0, 0, 32768, 83850, 148100, -3404, 4, 55, 55, 'Shots B-Grade', 0, 0),
('giran_sup6', 200000046, 'GiranSup6', 65, 3000, 3000, 1000, 1000, 0, 0, 1, 0, 49152, 83900, 148140, -3404, 4, 55, 55, 'Shots A-Grade', 0, 0),
('giran_sup7', 200000047, 'GiranSup7', 70, 3200, 3200, 1100, 1100, 1, 1, 2, 0, 0, 83950, 148180, -3404, 4, 55, 55, 'Shots S-Grade', 0, 0),
('giran_sup8', 200000048, 'GiranSup8', 60, 2200, 2200, 600, 600, 2, 2, 0, 0, 16384, 84000, 148220, -3404, 0, 9, 9, 'GHP, QHP & GCP', 0, 0),
('giran_sup9', 200000049, 'GiranSup9', 65, 3000, 3000, 1000, 1000, 0, 1, 1, 0, 32768, 84050, 148260, -3404, 4, 54, 54, 'Buy Cristais B/A/S', 0, 0),
('giran_sup10', 200000050, 'GiranSup10', 70, 3200, 3200, 1100, 1100, 1, 0, 0, 0, 49152, 84100, 148300, -3404, 4, 56, 56, 'Craft SS B/A/S', 0, 0),

('giran_eq1', 200000051, 'GiranEquip1', 55, 2500, 2500, 600, 600, 0, 0, 0, 0, 0, 83400, 147500, -3404, 0, 1, 1, 'Samurai Longsword +4', 0, 0),
('giran_eq2', 200000052, 'GiranEquip2', 55, 2200, 2200, 650, 650, 1, 1, 1, 0, 16384, 83450, 147540, -3404, 2, 22, 22, 'Crystal Dagger +4', 0, 0),
('giran_eq3', 200000053, 'GiranEquip3', 55, 2100, 2100, 700, 700, 2, 2, 2, 0, 32768, 83500, 147580, -3404, 1, 22, 22, 'Eminence Bow +4', 0, 0),
('giran_eq4', 200000054, 'GiranEquip4', 60, 2800, 2800, 650, 650, 0, 1, 0, 0, 49152, 83550, 147620, -3404, 0, 1, 1, 'Great Sword B +4', 0, 0),
('giran_eq5', 200000055, 'GiranEquip5', 60, 2800, 2800, 650, 650, 1, 0, 2, 0, 0, 83600, 147660, -3404, 0, 1, 1, 'Keshanberk B', 0, 0),
('giran_eq6', 200000056, 'GiranEquip6', 60, 2400, 2400, 700, 700, 2, 1, 0, 0, 16384, 83650, 147700, -3404, 2, 22, 22, 'Kris Dagger B', 0, 0),
('giran_eq7', 200000057, 'GiranEquip7', 60, 2300, 2300, 750, 750, 0, 2, 1, 0, 32768, 83700, 147740, -3404, 1, 22, 22, 'Bow of Peril B +4', 0, 0),
('giran_eq8', 200000058, 'GiranEquip8', 60, 1800, 1800, 1600, 1600, 1, 0, 0, 0, 49152, 83750, 147780, -3404, 0, 11, 11, 'Sword of Valhalla B', 0, 0),
('giran_eq9', 200000059, 'GiranEquip9', 60, 3000, 3000, 600, 600, 2, 1, 1, 0, 0, 83800, 147820, -3404, 3, 45, 45, 'Zubei Heavy Set', 0, 0),
('giran_eq10', 200000060, 'GiranEquip10', 60, 3000, 3000, 600, 600, 0, 2, 2, 0, 16384, 83850, 147860, -3404, 3, 45, 45, 'Avadon Heavy Set', 0, 0),
('giran_eq11', 200000061, 'GiranEquip11', 60, 3100, 3100, 600, 600, 1, 0, 0, 0, 32768, 83900, 147900, -3404, 3, 45, 45, 'Blue Wolf Heavy Set', 0, 0),
('giran_eq12', 200000062, 'GiranEquip12', 60, 1800, 1800, 1600, 1600, 2, 1, 0, 0, 49152, 83950, 147940, -3404, 0, 11, 11, 'Avadon Robe Set', 0, 0),
('giran_eq13', 200000063, 'GiranEquip13', 65, 3000, 3000, 1000, 1000, 0, 0, 1, 0, 0, 84000, 147980, -3404, 4, 56, 56, 'Craft Keshanberk', 0, 0),
('giran_eq14', 200000064, 'GiranEquip14', 65, 3000, 3000, 1000, 1000, 1, 1, 2, 0, 16384, 84050, 148020, -3404, 4, 56, 56, 'Craft Bow of Peril', 0, 0),
('giran_eq15', 200000065, 'GiranEquip15', 65, 3000, 3000, 1000, 1000, 2, 2, 0, 0, 32768, 84100, 148060, -3404, 4, 56, 56, 'Craft Blue Wolf Set', 0, 0),

('giran_top1', 200000066, 'GiranTop1', 70, 3400, 3400, 800, 800, 0, 0, 0, 0, 0, 83400, 147000, -3404, 0, 88, 88, 'Tallum Blade A +4', 0, 0),
('giran_top2', 200000067, 'GiranTop2', 70, 3400, 3400, 800, 800, 1, 1, 1, 0, 16384, 83450, 147040, -3404, 0, 88, 88, 'Dark Legion A', 0, 0),
('giran_top3', 200000068, 'GiranTop3', 70, 2800, 2800, 900, 900, 2, 2, 2, 0, 32768, 83500, 147080, -3404, 2, 93, 93, 'Soul Separator A +4', 0, 0),
('giran_top4', 200000069, 'GiranTop4', 70, 2700, 2700, 950, 950, 0, 1, 0, 0, 49152, 83550, 147120, -3404, 1, 92, 92, 'Soul Bow A +4', 0, 0),
('giran_top5', 200000070, 'GiranTop5', 70, 2200, 2200, 2000, 2000, 1, 0, 2, 0, 0, 83600, 147160, -3404, 0, 94, 94, 'Sword of Miracles A', 0, 0),
('giran_top6', 200000071, 'GiranTop6', 70, 3800, 3800, 800, 800, 2, 1, 0, 0, 16384, 83650, 147200, -3404, 3, 90, 90, 'Tallum Heavy Set A', 0, 0),
('giran_top7', 200000072, 'GiranTop7', 70, 2200, 2200, 2000, 2000, 0, 2, 1, 0, 32768, 83700, 147240, -3404, 0, 94, 94, 'Dark Crystal Robe A', 0, 0),
('giran_top8', 200000073, 'GiranTop8', 70, 2900, 2900, 900, 900, 1, 0, 0, 0, 49152, 83750, 147280, -3404, 2, 93, 93, 'Majestic Leather A', 0, 0),
('giran_top9', 200000074, 'GiranTop9', 80, 4200, 4200, 1000, 1000, 2, 1, 1, 0, 0, 83800, 147320, -3404, 0, 88, 88, 'Forgotten Blade S +4', 0, 0),
('giran_top10', 200000075, 'GiranTop10', 80, 3500, 3500, 1100, 1100, 0, 2, 2, 0, 16384, 83850, 147360, -3404, 2, 93, 93, 'Angel Slayer S +4', 0, 0),
('giran_top11', 200000076, 'GiranTop11', 80, 3400, 3400, 1150, 1150, 1, 0, 0, 0, 32768, 83900, 147400, -3404, 1, 92, 92, 'Draconic Bow S +4', 0, 0),
('giran_top12', 200000077, 'GiranTop12', 80, 2600, 2600, 2500, 2500, 2, 1, 0, 0, 49152, 83950, 147440, -3404, 0, 94, 94, 'Arcana Mace S +4', 0, 0),
('giran_top13', 200000078, 'GiranTop13', 80, 4500, 4500, 1000, 1000, 0, 0, 1, 0, 0, 84000, 147480, -3404, 0, 88, 88, 'Imperial Crusader Set', 0, 0),
('giran_top14', 200000079, 'GiranTop14', 80, 3500, 3500, 1100, 1100, 1, 1, 2, 0, 16384, 84050, 147520, -3404, 1, 92, 92, 'Draconic Leather Set', 0, 0),
('giran_top15', 200000080, 'GiranTop15', 80, 2600, 2600, 2500, 2500, 2, 2, 0, 0, 32768, 84100, 147560, -3404, 0, 94, 94, 'Major Arcana Robe Set', 0, 0),

-- Aden (20)
('aden_e1', 200000081, 'AdenElite1', 85, 5000, 5000, 1200, 1200, 0, 0, 0, 0, 0, 147450, 25900, -2012, 0, 88, 88, 'Dynasty Sword +4/+8', 0, 0),
('aden_e2', 200000082, 'AdenElite2', 85, 4000, 4000, 1300, 1300, 1, 1, 1, 0, 16384, 147500, 25940, -2012, 1, 92, 92, 'Dynasty Bow +4/+8', 0, 0),
('aden_e3', 200000083, 'AdenElite3', 85, 5000, 5000, 1200, 1200, 2, 2, 2, 0, 32768, 147550, 25980, -2012, 0, 88, 88, 'Icarus Sawsword +4/+8', 0, 0),
('aden_e4', 200000084, 'AdenElite4', 85, 4200, 4200, 1250, 1250, 0, 1, 0, 0, 49152, 147600, 26020, -2012, 2, 93, 93, 'Icarus Disperser +4/+8', 0, 0),
('aden_e5', 200000085, 'AdenElite5', 85, 3000, 3000, 3000, 3000, 1, 0, 2, 0, 0, 147650, 26060, -2012, 0, 94, 94, 'Icarus Spirit +4/+8', 0, 0),

('aden_e6', 200000086, 'AdenElite6', 85, 5200, 5200, 1200, 1200, 2, 1, 0, 0, 16384, 147450, 25800, -2012, 0, 88, 88, 'Vesper Cutter +4/+8', 0, 0),
('aden_e7', 200000087, 'AdenElite7', 85, 4200, 4200, 1300, 1300, 0, 2, 1, 0, 32768, 147500, 25760, -2012, 1, 92, 92, 'Vesper Thrower +4/+8', 0, 0),
('aden_e8', 200000088, 'AdenElite8', 85, 4300, 4300, 1250, 1250, 1, 0, 0, 0, 49152, 147550, 25720, -2012, 2, 93, 93, 'Skull Edge +4/+8', 0, 0),
('aden_e9', 200000089, 'AdenElite9', 85, 4200, 4200, 1300, 1300, 2, 1, 1, 0, 0, 147600, 25680, -2012, 1, 92, 92, 'Cyclone Bow S84 +4/+8', 0, 0),
('aden_e10', 200000090, 'AdenElite10', 85, 3000, 3000, 3000, 3000, 0, 2, 2, 0, 16384, 147650, 25640, -2012, 0, 94, 94, 'Sacredium S84 +4/+8', 0, 0),

('aden_e11', 200000091, 'AdenElite11', 85, 5500, 5500, 1200, 1200, 1, 0, 0, 0, 32768, 147700, 25900, -2012, 0, 88, 88, 'Vesper Noble Set +4/+8', 0, 0),
('aden_e12', 200000092, 'AdenElite12', 85, 4500, 4500, 1300, 1300, 2, 1, 0, 0, 49152, 147750, 25940, -2012, 2, 93, 93, 'Vorpal Armor Sets', 0, 0),
('aden_e13', 200000093, 'AdenElite13', 85, 5800, 5800, 1200, 1200, 0, 0, 1, 0, 0, 147800, 25980, -2012, 0, 88, 88, 'Elegy Supreme Sets', 0, 0),
('aden_e14', 200000094, 'AdenElite14', 85, 3000, 3000, 3000, 3000, 1, 1, 2, 0, 16384, 147850, 26020, -2012, 0, 94, 94, 'Vorpal & Vesper Jewels', 0, 0),
('aden_e15', 200000095, 'AdenElite15', 85, 3000, 3000, 3000, 3000, 2, 2, 0, 0, 32768, 147900, 26060, -2012, 0, 94, 94, 'Elegy Jewels S84', 0, 0),

('aden_e16', 200000096, 'AdenElite16', 85, 3000, 3000, 3000, 3000, 0, 1, 0, 0, 49152, 147700, 25800, -2012, 0, 94, 94, 'Attribute Stones', 0, 0),
('aden_e17', 200000097, 'AdenElite17', 85, 3000, 3000, 3000, 3000, 1, 2, 1, 0, 0, 147750, 25760, -2012, 0, 94, 94, 'Attribute Crystals', 0, 0),
('aden_e18', 200000098, 'AdenElite18', 85, 3500, 3500, 2000, 2000, 2, 0, 2, 0, 16384, 147800, 25720, -2012, 4, 54, 54, 'Blessed Enchants S', 0, 0),
('aden_e19', 200000099, 'AdenElite19', 85, 3000, 3000, 3000, 3000, 0, 1, 0, 0, 32768, 147850, 25680, -2012, 0, 94, 94, 'Top Life Stones 80-84', 0, 0),
('aden_e20', 2000000100, 'AdenElite20', 85, 3000, 3000, 3000, 3000, 1, 0, 1, 0, 49152, 147900, 25640, -2012, 0, 94, 94, 'Giants Codex & Mastery', 0, 0),

-- Cidades Secundárias (30)
('oren_s1', 200000101, 'OrenShop1', 50, 2000, 2000, 600, 600, 0, 0, 0, 0, 0, 82698, 53239, -1495, 0, 9, 9, 'Potions & Shots Oren', 0, 0),
('oren_s2', 200000102, 'OrenShop2', 50, 2200, 2200, 650, 650, 1, 1, 1, 0, 16384, 82740, 53270, -1495, 4, 53, 53, 'Materiais Oren', 0, 0),
('oren_s3', 200000103, 'OrenShop3', 50, 2000, 2000, 600, 600, 2, 2, 2, 0, 32768, 82780, 53300, -1495, 0, 9, 9, 'Scrolls Escape & Res', 0, 0),
('oren_s4', 200000104, 'OrenShop4', 50, 2200, 2200, 650, 650, 0, 1, 0, 0, 49152, 82650, 53200, -1495, 4, 54, 54, 'Buy Iron & Bone', 0, 0),
('oren_s5', 200000105, 'OrenShop5', 52, 2400, 2400, 700, 700, 1, 0, 2, 0, 0, 82610, 53160, -1495, 4, 56, 56, 'Craft SS C Oren', 0, 0),

('hunters_s1', 200000106, 'HuntersShop1', 55, 2300, 2300, 700, 700, 2, 1, 0, 0, 16384, 116550, 75750, -2700, 1, 22, 22, 'Suprimentos Caca', 0, 0),
('hunters_s2', 200000107, 'HuntersShop2', 55, 2500, 2500, 750, 750, 0, 2, 1, 0, 32768, 116600, 75790, -2700, 4, 53, 53, 'Couro & Thread Hunters', 0, 0),
('hunters_s3', 200000108, 'HuntersShop3', 55, 2500, 2500, 750, 750, 1, 0, 0, 0, 49152, 116650, 75830, -2700, 4, 55, 55, 'Shots B-Grade Hunters', 0, 0),
('hunters_s4', 200000109, 'HuntersShop4', 55, 2500, 2500, 750, 750, 2, 1, 1, 0, 0, 116500, 75710, -2700, 4, 54, 54, 'Buy Skin & Stem', 0, 0),
('hunters_s5', 200000110, 'HuntersShop5', 60, 2800, 2800, 850, 850, 0, 2, 2, 0, 16384, 116450, 75670, -2700, 4, 56, 56, 'Craft SS B Hunters', 0, 0),

('heine_s1', 200000111, 'HeineShop1', 50, 2000, 2000, 600, 600, 1, 0, 0, 0, 32768, 111394, 219354, -3544, 0, 9, 9, 'Suprimentos Heine', 0, 0),
('heine_s2', 200000112, 'HeineShop2', 50, 2200, 2200, 650, 650, 2, 1, 0, 0, 49152, 111440, 219390, -3544, 4, 53, 53, 'Materiais Heine', 0, 0),
('heine_s3', 200000113, 'HeineShop3', 50, 2200, 2200, 650, 650, 0, 0, 1, 0, 0, 111490, 219430, -3544, 4, 55, 55, 'Shots C-Grade Heine', 0, 0),
('heine_s4', 200000114, 'HeineShop4', 50, 2200, 2200, 650, 650, 1, 1, 2, 0, 16384, 111350, 219310, -3544, 4, 54, 54, 'Buy Insumos Heine', 0, 0),
('heine_s5', 200000115, 'HeineShop5', 52, 2400, 2400, 700, 700, 2, 2, 0, 0, 32768, 111300, 219270, -3544, 4, 56, 56, 'Craft SS C Heine', 0, 0),

('goddard_s1', 200000116, 'GoddardShop1', 75, 3200, 3200, 1000, 1000, 0, 1, 0, 0, 49152, 147920, -55300, -2730, 0, 9, 9, 'Potions & Res Goddard', 0, 0),
('goddard_s2', 200000117, 'GoddardShop2', 75, 3500, 3500, 1100, 1100, 1, 2, 1, 0, 0, 147960, -55260, -2730, 4, 53, 53, 'Materiais Goddard', 0, 0),
('goddard_s3', 200000118, 'GoddardShop3', 75, 3500, 3500, 1100, 1100, 2, 0, 2, 0, 16384, 148000, -55220, -2730, 4, 55, 55, 'Shots A/S Goddard', 0, 0),
('goddard_s4', 200000119, 'GoddardShop4', 75, 3500, 3500, 1100, 1100, 0, 1, 0, 0, 32768, 147880, -55340, -2730, 4, 54, 54, 'Buy Steel Goddard', 0, 0),
('goddard_s5', 200000120, 'GoddardShop5', 75, 3800, 3800, 1200, 1200, 1, 0, 1, 0, 49152, 147840, -55380, -2730, 4, 56, 56, 'Craft SS S Goddard', 0, 0),

('rune_s1', 200000121, 'RuneShop1', 75, 3200, 3200, 1000, 1000, 0, 0, 0, 0, 0, 43800, -47700, -790, 0, 9, 9, 'Potions & Res Rune', 0, 0),
('rune_s2', 200000122, 'RuneShop2', 75, 3500, 3500, 1100, 1100, 1, 1, 1, 0, 16384, 43840, -47660, -790, 4, 53, 53, 'Materiais Raros Rune', 0, 0),
('rune_s3', 200000123, 'RuneShop3', 75, 3500, 3500, 1100, 1100, 2, 2, 2, 0, 32768, 43880, -47620, -790, 4, 55, 55, 'Shots A/S Rune', 0, 0),
('rune_s4', 200000124, 'RuneShop4', 75, 3500, 3500, 1100, 1100, 0, 1, 0, 0, 49152, 43760, -47740, -790, 4, 54, 54, 'Buy Bone Powder Rune', 0, 0),
('rune_s5', 200000125, 'RuneShop5', 75, 3800, 3800, 1200, 1200, 1, 0, 2, 0, 0, 43720, -47780, -790, 4, 56, 56, 'Craft SS S Rune', 0, 0),

('schutt_s1', 200000126, 'SchuttgartShop1', 55, 2200, 2200, 600, 600, 2, 1, 0, 0, 16384, 87300, -142300, -1340, 0, 9, 9, 'Suprimentos Schuttgart', 0, 0),
('schutt_s2', 200000127, 'SchuttgartShop2', 55, 2500, 2500, 700, 700, 0, 2, 1, 0, 32768, 87340, -142260, -1340, 4, 53, 53, 'Iron & Coal Schuttgart', 0, 0),
('schutt_s3', 200000128, 'SchuttgartShop3', 55, 2500, 2500, 700, 700, 1, 0, 0, 0, 49152, 87380, -142220, -1340, 4, 55, 55, 'Shots C & B Schuttgart', 0, 0),
('schutt_s4', 200000129, 'SchuttgartShop4', 55, 2500, 2500, 700, 700, 2, 1, 1, 0, 0, 87260, -142340, -1340, 4, 54, 54, 'Buy Coal Schuttgart', 0, 0),
('schutt_s5', 200000130, 'SchuttgartShop5', 55, 2800, 2800, 800, 800, 0, 2, 2, 0, 16384, 87220, -142380, -1340, 4, 56, 56, 'Craft SS B Schuttgart', 0, 0);

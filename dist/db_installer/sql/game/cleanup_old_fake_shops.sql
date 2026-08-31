DELETE FROM `character_skills` WHERE `charId` IN (SELECT `charId` FROM `characters` WHERE `char_name` LIKE 'GiranSup%' OR `char_name` LIKE 'DionTrader%' OR `char_name` LIKE 'AdenElite%' OR `char_name` LIKE 'RuneShop%' OR `char_name` LIKE 'OrenShop%' OR `char_name` LIKE 'HuntersShop%' OR `char_name` LIKE 'HeineShop%' OR `char_name` LIKE 'GoddardShop%' OR `char_name` LIKE 'SchuttgartShop%' OR `char_name` LIKE 'GiranMat%' OR `char_name` LIKE 'GiranEquip%' OR `char_name` LIKE 'GiranTop%');
DELETE FROM `items` WHERE `owner_id` IN (SELECT `charId` FROM `characters` WHERE `char_name` LIKE 'GiranSup%' OR `char_name` LIKE 'DionTrader%' OR `char_name` LIKE 'AdenElite%' OR `char_name` LIKE 'RuneShop%' OR `char_name` LIKE 'OrenShop%' OR `char_name` LIKE 'HuntersShop%' OR `char_name` LIKE 'HeineShop%' OR `char_name` LIKE 'GoddardShop%' OR `char_name` LIKE 'SchuttgartShop%' OR `char_name` LIKE 'GiranMat%' OR `char_name` LIKE 'GiranEquip%' OR `char_name` LIKE 'GiranTop%');

DELETE FROM `characters` WHERE 
`char_name` LIKE 'GiranSup%' OR 
`char_name` LIKE 'DionTrader%' OR 
`char_name` LIKE 'AdenElite%' OR 
`char_name` LIKE 'RuneShop%' OR 
`char_name` LIKE 'OrenShop%' OR 
`char_name` LIKE 'HuntersShop%' OR 
`char_name` LIKE 'HeineShop%' OR 
`char_name` LIKE 'GoddardShop%' OR 
`char_name` LIKE 'SchuttgartShop%' OR 
`char_name` LIKE 'GiranMat%' OR 
`char_name` LIKE 'GiranEquip%' OR 
`char_name` LIKE 'GiranTop%';

DELETE FROM `accounts` WHERE 
`login` LIKE 'giran_%' OR 
`login` LIKE 'dion_%' OR 
`login` LIKE 'aden_%' OR 
`login` LIKE 'rune_%' OR 
`login` LIKE 'oren_%' OR 
`login` LIKE 'hunters_%' OR 
`login` LIKE 'heine_%' OR 
`login` LIKE 'goddard_%' OR 
`login` LIKE 'schuttgart_%';

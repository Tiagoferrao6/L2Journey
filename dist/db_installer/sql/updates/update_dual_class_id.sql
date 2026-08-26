-- Update script to ensure dual_class_id and fake_players_profiles columns exist
ALTER TABLE `characters` ADD COLUMN IF NOT EXISTS `dual_class_id` INT(2) NOT NULL DEFAULT '-1' AFTER `base_class`;
ALTER TABLE `character_subclasses` ADD COLUMN IF NOT EXISTS `dual_class_id` INT(2) NOT NULL DEFAULT '-1' AFTER `class_id`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `dual_class_id` INT(2) NOT NULL DEFAULT '-1' AFTER `class_id`;

ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `agressividade` INT NOT NULL DEFAULT 5 AFTER `dual_class_id`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `coragem` INT NOT NULL DEFAULT 5 AFTER `agressividade`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `party_tendency` INT NOT NULL DEFAULT 5 AFTER `coragem`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `turno` VARCHAR(20) NOT NULL DEFAULT 'DAY' AFTER `party_tendency`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `zone_id` VARCHAR(50) NOT NULL DEFAULT 'GLUDIO' AFTER `turno`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `x` INT DEFAULT 0 AFTER `zone_id`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `y` INT DEFAULT 0 AFTER `x`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `z` INT DEFAULT 0 AFTER `y`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `heading` INT DEFAULT 0 AFTER `z`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `inventory` TEXT DEFAULT NULL AFTER `heading`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `party_id` INT DEFAULT 0 AFTER `inventory`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `is_active` TINYINT(1) DEFAULT 0 AFTER `party_id`;
ALTER TABLE `fake_players_profiles` ADD COLUMN IF NOT EXISTS `last_active_time` BIGINT DEFAULT 0 AFTER `is_active`;

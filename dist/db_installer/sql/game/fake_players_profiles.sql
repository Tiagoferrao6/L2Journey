CREATE TABLE IF NOT EXISTS `fake_players_profiles` (
  `fake_id` INT NOT NULL AUTO_INCREMENT,
  `bot_type` VARCHAR(20) NOT NULL DEFAULT 'HUNTER', -- TRADER or HUNTER
  `class_id` INT NOT NULL DEFAULT 0,
  `dual_class_id` INT NOT NULL DEFAULT -1,
  `agressividade` INT NOT NULL DEFAULT 5, -- 1 to 10
  `coragem` INT NOT NULL DEFAULT 5, -- 1 to 10
  `party_tendency` INT NOT NULL DEFAULT 5, -- 1 to 10
  `turno` VARCHAR(20) NOT NULL DEFAULT 'DAY', -- DAY, NIGHT, 4H, 8H
  `zone_id` VARCHAR(50) NOT NULL DEFAULT 'GLUDIO',
  `x` INT DEFAULT 0,
  `y` INT DEFAULT 0,
  `z` INT DEFAULT 0,
  `heading` INT DEFAULT 0,
  `inventory` TEXT DEFAULT NULL,
  `party_id` INT DEFAULT 0,
  `is_active` TINYINT(1) DEFAULT 0,
  `last_active_time` BIGINT DEFAULT 0,
  PRIMARY KEY (`fake_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

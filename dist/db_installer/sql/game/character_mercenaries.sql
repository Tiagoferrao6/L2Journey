CREATE TABLE IF NOT EXISTS `character_mercenaries` (
  `char_id` INT UNSIGNED NOT NULL,
  `mercenary_id` VARCHAR(50) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `level` INT NOT NULL,
  `exp` BIGINT NOT NULL DEFAULT 0,
  `sp` INT NOT NULL DEFAULT 0,
  `class_id` INT NOT NULL,
  PRIMARY KEY (`char_id`, `mercenary_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

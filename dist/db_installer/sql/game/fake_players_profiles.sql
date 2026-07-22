CREATE TABLE IF NOT EXISTS `fake_players_profiles` (
  `fake_id` INT NOT NULL AUTO_INCREMENT,
  `bot_type` VARCHAR(20) NOT NULL DEFAULT 'HUNTER', -- e.g. TRADER or HUNTER
  `class_id` INT NOT NULL DEFAULT 0, -- the class of the bot
  `agressividade` INT NOT NULL DEFAULT 5, -- 1 to 10
  `coragem` INT NOT NULL DEFAULT 5, -- 1 to 10
  `turno` VARCHAR(20) NOT NULL DEFAULT 'DAY', -- the shift of the bot, e.g. DAY, NIGHT, 1H
  `zone_id` VARCHAR(50) NOT NULL DEFAULT 'GLUDIO', -- home zone
  PRIMARY KEY (`fake_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

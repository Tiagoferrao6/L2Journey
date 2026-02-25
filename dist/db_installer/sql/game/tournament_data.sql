-- =========================================================================
-- Tournament PvP Event - Ranking Table
-- =========================================================================
-- Execute this SQL on the game database before using the Tournament event.
-- Supports MySQL / MariaDB with ON DUPLICATE KEY UPDATE.
-- =========================================================================

CREATE TABLE IF NOT EXISTS `tournament_ranking` (
  `char_id` int(10) unsigned NOT NULL,
  `char_name` varchar(35) NOT NULL DEFAULT '',
  `fight_type` tinyint(3) unsigned NOT NULL DEFAULT 1 COMMENT '1=1x1, 2=2x2, 3=3x3, 4=4x4, 5=5x5, 9=9x9',
  `victories` int(10) unsigned NOT NULL DEFAULT 0,
  `defeats` int(10) unsigned NOT NULL DEFAULT 0,
  `ties` int(10) unsigned NOT NULL DEFAULT 0,
  `kills` int(10) unsigned NOT NULL DEFAULT 0,
  `deaths` int(10) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_id`, `fight_type`),
  KEY `idx_fight_type_victories` (`fight_type`, `victories` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `character_llm_relationships` (
  `bot_object_id` INT NOT NULL,
  `target_object_id` INT NOT NULL,
  `target_name` VARCHAR(45) NOT NULL,
  `affinity_score` INT NOT NULL DEFAULT 0,
  `relationship_status` VARCHAR(32) NOT NULL DEFAULT 'NEUTRAL',
  `last_interaction_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`bot_object_id`, `target_object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

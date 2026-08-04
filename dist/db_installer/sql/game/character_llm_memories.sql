CREATE TABLE IF NOT EXISTS `character_llm_memories` (
  `memory_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `bot_object_id` INT NOT NULL,
  `target_object_id` INT NOT NULL,
  `event_type` VARCHAR(32) NOT NULL,
  `description` TEXT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  KEY `idx_bot_target` (`bot_object_id`, `target_object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

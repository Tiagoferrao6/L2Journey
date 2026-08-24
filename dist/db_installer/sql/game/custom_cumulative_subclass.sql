-- Patch SQL para o Sistema de Subclasse Acumulativa (Dual Class)

ALTER TABLE `characters` 
  ADD COLUMN `dual_class_id` INT(2) NOT NULL DEFAULT '-1' AFTER `base_class`;

ALTER TABLE `character_subclasses` 
  ADD COLUMN `dual_class_id` INT(2) NOT NULL DEFAULT '-1' AFTER `class_id`;

ALTER TABLE `fake_players_profiles`
  ADD COLUMN `dual_class_id` INT(2) NOT NULL DEFAULT '-1' AFTER `class_id`;

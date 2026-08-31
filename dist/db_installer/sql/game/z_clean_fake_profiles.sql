-- L2Journey DB Cleanup
-- Execute este script caso você tenha iniciado o servidor com o FakePlayerManager auto-populando a tabela.
-- Isso apagará todos os Traders ou bots defeituosos injetados automaticamente.

TRUNCATE TABLE `fake_players_profiles`;

-- Se você já inseriu Hunters válidos manualmente que quer preservar, use:
-- DELETE FROM `fake_players_profiles` WHERE `bot_type` = 'TRADER';

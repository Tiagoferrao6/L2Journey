# Design: Mercenary Healer Architecture (On-Demand, Persistence & Contract Reload)

## Architecture Overview

```
┌──────────────────────────────────────────────────────────────────────────┐
│               MERCENARY HEALER SYSTEM ARCHITECTURE                       │
├───────────────────────────────────┬──────────────────────────────────────┤
│ 1. MercenaryManager               │ 2. Database Persistence (MariaDB)    │
│    • `EnableMercenaries` ini check│    • Tabela `character_mercenaries`.  │
│    • On-Demand instantiation.     │    • Salva bot_id, level, exp, sp,   │
│    • Reload/Reset logic (1 Adena).│      char_id, active_profile.        │
├───────────────────────────────────┼──────────────────────────────────────┤
│ 3. Community Board (`Alt + B`)    │ 4. Healer AI & Auto-Scale            │
│    • [Contratar Healer (1 Adena)] │    • Emergency Heal (HP < 70%).      │
│    • [Resetar Contrato (1 Adena)] │    • Cleanse / Resurrection.         │
│    • [Seguir] / [Ancorar].        │    • Auto-equip por tier de level.   │
└───────────────────────────────────┴──────────────────────────────────────┘
```

## Detailed Specifications

### 1. Database Schema (`character_mercenaries`)
```sql
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
```

### 2. On-Demand Spawn & Player Level Matching
- Quando o jogador clica em **"Contratar Mercenário"**:
  1. Verifica se o jogador tem Adena suficiente (`Config.MERCENARY_HIRE_FEE` = 1 Adena).
  2. Obtém o nível atual do jogador real (`player.getLevel()`).
  3. Instancia o `MercenaryInstance` (subclasse de `FakePlayer`) configurado no nível do jogador.
  4. Atribui o set de armadura e arma correspondente à faixa de nível (ex: Carmian no Lvl 40, Dark Crystal no Lvl 61+).
  5. Insere na Party e salva o registro em `character_mercenaries`.

### 3. Reload / Reset Contract Logic
- Quando o jogador clica em **"Resetar Contrato / Reload"**:
  1. Cobra a taxa de 1 Adena.
  2. Remove o Mercenário atual do mundo e da Party.
  3. Atualiza o registro no banco de dados para o nível atual do jogador (`player.getLevel()`).
  4. Re-instancia o Mercenário Healer com HP/MP cheios, skills atualizadas para o nível e armaduras/armas ajustadas para a nova faixa de level.

### 4. Configuration Parameters (`fakeplayers.ini`)
```ini
# Ativa o sistema de Contratação de Mercenários in-game (Healer).
EnableMercenaries = True

# Custo em Adena para contratar ou resetar o contrato do Mercenário.
MercenaryHireFee = 1
```

## Context

The character `SilverTester` (`charId = 300000000`) is the dedicated end-to-end test character in the MariaDB database (`l2journey`). To thoroughly test high-end gameplay, cumulative subclass configurations (Sword Muse skills on Moonlight Sentinel), tattoo bonuses, consumable balance, and raid boss access, we need a repeatable automated SQL script to set up character state.

## Goals / Non-Goals

**Goals:**
- Provide a clear, idempotent SQL setup script to configure `SilverTester`.
- Set Adena balance to 1,000,000,000.
- Populate exact requested amounts of combat consumables (500x CP, 500x HP, 500x MP, 100x SOE, 100x BRES, 100k SS/BSS).
- Populate all 14 Level 6 Tattoos (Right and Left slots).
- Provide boss access items, enhancement codes, and life stones.
- Inject `Sword Muse` (Class ID 107) song skills into `SilverTester`'s main class (`Moonlight Sentinel`, Class ID 102).

**Non-Goals:**
- Modifying player characters other than `SilverTester` (`300000000`).
- Modifying L2 server core Java source code or XML stat definitions.

## Decisions

### Decision 1: Direct SQL Script via Podman MariaDB Exec
- **Rationale**: Direct SQL execution in MariaDB (`podman exec l2journey_db_1 mariadb ...`) guarantees instant, predictable setup without needing to restart the game server or execute manual admin commands.
- **Alternatives Considered**: In-game GM commands (`//give_item`, `//add_skill`). Rejected because doing it manually for 30+ items and dozens of skills is error-prone and non-reproducible.

### Decision 2: Unique Item Object IDs Generation
- **Rationale**: Using `MAX(object_id) + offset` when inserting items into the `items` database table ensures primary key constraint compliance and prevents conflicts with existing server items.

## Risks / Trade-offs

- **[Risk] Character Online state during SQL execution** → **Mitigation**: Ensure `SilverTester` is offline (`online = 0`) before executing database updates, or restart/reload character cache afterwards.
- **[Risk] Duplicate skill entries in `character_skills`** → **Mitigation**: Use `INSERT IGNORE` or `REPLACE INTO character_skills` to safely update skill levels.

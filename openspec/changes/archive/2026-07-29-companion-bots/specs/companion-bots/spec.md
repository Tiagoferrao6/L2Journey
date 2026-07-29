# Capability: Mercenary Healer System (MVP)

## Specification

### REQUIREMENT: On-Demand Mercenary Healer Creation
The system MUST allow a player to hire a Mercenary Healer on-demand via the Community Board (`Alt + B`). The mercenary MUST be instantiated immediately at the player's current level for a fee of 1 Adena.

#### Scenario: Hiring a Healer at Player Level
- GIVEN a player is at level 45 with at least 1 Adena
- WHEN the player clicks "Contratar Mercenário Healer" in `Alt + B`
- THEN 1 Adena is deducted, a Healer FakePlayer is spawned at level 45, equipped with level-appropriate gear, added to the party, and saved to `character_mercenaries`.

### REQUIREMENT: Contract Reload and Level Resynchronization
The system MUST provide a "Resetar Contrato / Reload" button in the Community Board (`Alt + B`) for a fee of 1 Adena, resetting the mercenary to the player's current level, restoring HP/MP, and equipping matching gear tier.

#### Scenario: Resynchronizing a Mercenary Contract
- GIVEN a player has leveled up from 45 to 65 and has a hired Healer
- WHEN the player clicks "Resetar Contrato / Reload" in `Alt + B`
- THEN 1 Adena is deducted, the mercenary is despawned, re-instantiated at level 65 with updated skills and A-Grade equipment, and re-added to the party.

### REQUIREMENT: Persistence in MariaDB
The mercenary's state (level, EXP, SP) MUST be stored in the `character_mercenaries` table in MariaDB so that it persists across player logouts and server restarts.

#### Scenario: Relogging with Hired Mercenary
- GIVEN a player with a saved mercenary logs out
- WHEN the player logs back into the game
- THEN the mercenary is automatically re-instantiated from `character_mercenaries` with all saved progress intact.

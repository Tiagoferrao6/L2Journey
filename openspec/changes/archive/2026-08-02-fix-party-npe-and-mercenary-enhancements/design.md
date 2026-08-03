# Design Document: Party NPE Safety, Single Mercenary Enforcer & Pet Command Window Integration

## Context
Players hiring mercenaries require a robust system that prevents NPE crashes during party setup, guarantees a maximum of 1 active mercenary per player, and provides native UI controls (`ALT+Y` Action Window) alongside automated BabyPet support AI.

## Goals / Non-Goals

**Goals:**
- Guarantee `getPartyDistributionType()` never returns `null` in `Player.java` and packet writers.
- Ensure strict 1-mercenary per player constraint in `MercenaryManager.java`.
- Open Pet Status HUD & `ALT+Y` Action Window on mercenary spawn.
- Handle Pet Actions (15 Follow, 16 Attack, 17 Stop, 19 Dismiss) for Mercenaries in `RequestActionUse.java`.
- Add a "Dispensar Mercenário" bypass (`_merc_dismiss`) in Community Board.

**Non-Goals:**
- Allowing multiple mercenaries per single player character.
- Modifying standard player-to-player party invitation flows.

## Decisions

### Decision 1: Strict Single Mercenary Enforcement
- In `MercenaryManager.hireMercenary(Player owner, String mercId)`:
  ```java
  dismissMercenary(owner, true); // Despawn & clear DB of any existing mercenary
  ```
  This guarantees that a player can never have more than 1 active mercenary instance in memory or database.

### Decision 2: Pet HUD & Action Window (`ALT+Y`) Packets
- When `MercenaryInstance` spawns:
  ```java
  owner.sendPacket(new PetStatusShow(merc));
  owner.sendPacket(new PetInfo(merc, 1));
  ```
  This triggers the Lineage II client to render the Pet Status bar and enable the `ALT+Y` Action Window buttons.

### Decision 3: Intercept Pet Actions in `RequestActionUse.java`
- If `player.getSummon() == null`, check for `MercenaryInstance merc = MercenaryManager.getInstance().getActiveMercenary(player.getObjectId())`:
  - `Action 15 / 21`: `merc.setFollowing(!merc.isFollowing());`
  - `Action 16 / 22`: `merc.setTarget(target); merc.doAttack(target);`
  - `Action 17 / 23`: `merc.abortAttack(); merc.abortCast();`
  - `Action 19`: `MercenaryManager.getInstance().dismissMercenary(player, true);`

## Risks / Trade-offs
- **[Risk]** Client packet compatibility for Pet HUD on a `Player` subclass entity.
  - **Mitigation:** Fallback to Party Window (`ALT+F`) and Community Board bypass (`_merc_dismiss`) ensures full functionality regardless of client frame rendering.

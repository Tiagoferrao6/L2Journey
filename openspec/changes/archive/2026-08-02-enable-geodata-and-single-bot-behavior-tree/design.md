# Design Document: Enable Geodata Pathfinding & Single-Bot Behavior Tree Testing

## Context
FakePlayer bots currently walk through walls and buildings when pathfinding is disabled. Additionally, their behavior is hardcoded in `FakeHunterManager.java`, performing instant teleports when within 180 units of a Gatekeeper NPC.

By enabling Geodata A* pathfinding and introducing a Behavior Tree (BT), a single test bot (`TestBot`) can navigate Gludio town center, dodge buildings, target the Gatekeeper NPC, and invoke native HTML bypasses (`onBypassFeedback`) like a real player.

## Architecture & Data Flow

```
                      ┌──────────────────────────┐
                      │ FakePlayerBehaviorTree   │
                      └────────────┬─────────────┘
                                   │
         ┌─────────────────────────┼─────────────────────────┐
         ▼                         ▼                         ▼
  ┌──────────────┐          ┌──────────────┐          ┌──────────────┐
  │ Emergency    │          │ Town Navigation│        │ Hunting Loop │
  │ Node         │          │ & NPC Dialog │          │ Node         │
  └──────┬───────┘          └──────┬───────┘          └──────┬───────┘
         │                         │                         │
   HP < 20%?                 WalkToGK (Geo A*)         Search Monster
   Flee / Potion             Target GK NPC             Cast Skill / Attack
                             Send Bypass ("goto 50012") Pickup Items
```

## Design Decisions

### Decision 1: Geodata Docker Mounting & Realtime Pathfinding (PathFinding = 2)
- Geodata files (`.l2j`) located in `./Geodata/` are mounted into `/opt/l2journey/game/data/geodata` via `docker-compose.yml`.
- `dist/game/config/admin/geodata.ini` sets `PathFinding = 2` (realtime A* path calculation) and `CoordSynchronize = 2`.

### Decision 2: Behavior Tree Node Interface
Create a lightweight modular BT pattern in `com.l2journey.gameserver.model.fake.ai.bt`:
- `BTNode`: interface with `BTStatus execute(FakePlayer bot)`.
- `BTSequence` & `BTSelector`: composite control nodes.
- `BTActionWalkToNpc`: navigates to NPC location using `GeoEngine.getInstance().findPath()`.
- `BTActionInteractBypass`: targets NPC, sends `onAction`, then sends `onBypassFeedback(bot, bypass)`.

### Decision 3: Single Bot Test Mode
- In `FakeHunterManager.java`, restrict active bot pool to 1 bot (`TestBot`) for clear visual observation during testing.

## Risks / Trade-offs
- **[Risk]** Memory usage increases slightly with Geodata loading.
  - **Mitigation:** The server container already has 2GB–4GB heap allocated (`-Xms2g -Xmx4g`), which easily accommodates Geodata.

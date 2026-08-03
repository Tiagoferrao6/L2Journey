# Proposal: Enable Geodata Pathfinding & Single-Bot Behavior Tree Testing

## Why
Currently, FakeHunters move in straight lines ignoring urban buildings because Geodata pathfinding is disabled (`PathFinding = 0`), and bots use hardcoded Java procedural loops with instant teleports instead of realistic player interactions. Additionally, running multiple bots makes observing and debugging individual bot navigation and NPC/mob interactions difficult.

Enabling real-time Geodata pathfinding (`PathFinding = 2`), configuring Docker volume mounts for the 205 `.l2j` map files, and introducing a modular **Behavior Tree (BT) / Script Tree** for a single test bot (`TestBot`) allows realistic urban pathing (dodging Gludio buildings), NPC Gatekeeper bypass interaction, and a structured decision-making model.

## What Changes
- **Geodata Activation & Docker Mounting**:
  - Update `dist/game/config/admin/geodata.ini`: set `PathFinding = 2`, `CoordSynchronize = 2`, `geodataPath = ./data/geodata`.
  - Update `docker-compose.yml`: mount `./Geodata` to `/opt/l2journey/game/data/geodata:ro` and `./dist/game/config/admin/geodata.ini` to `/opt/l2journey/game/config/admin/geodata.ini:ro`.
- **Single Test Bot Mode**:
  - Restrict spawned test FakeHunters to 1 single bot (`TestBot`) for clean visual observation.
- **Behavior Tree / Script System for FakePlayer**:
  - Introduce a modular Behavior Tree (`FakePlayerBehaviorTree`) with nodes for:
    - **Urban Navigation**: Walk from Gludio town center to Gatekeeper using Geodata pathfinding.
    - **NPC Interaction**: Target Gatekeeper NPC, invoke `onAction` and `onBypassFeedback` (simulate player HTML clicking for teleport).
    - **Decision Tree**: Select between Town Navigation, Hunting Sequence, and Emergency Flee.

## Capabilities

### New Capabilities
- `geodata-realtime-pathfinding`: Geodata pathfinding (`PathFinding = 2`) activation and Docker volume integration.
- `fake-player-behavior-tree`: Script/Behavior Tree decision engine for FakePlayers handling urban navigation and native NPC bypass interaction.

### Modified Capabilities
- None

## Impact
- **Backend / GameServer**:
  - `dist/game/config/admin/geodata.ini`: Config updates for A* pathfinding.
  - `docker-compose.yml`: Volume mappings for `./Geodata` and `geodata.ini`.
  - `com.l2journey.gameserver.managers.FakeHunterManager`: Single bot spawn mode and Behavior Tree tick execution.
  - `com.l2journey.gameserver.model.actor.instance.FakePlayer`: Integration with `FakePlayerBehaviorTree`.

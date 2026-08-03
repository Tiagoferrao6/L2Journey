# Proposal: Dedicated FakePlayer Live Monitoring Tab in GM Dashboard

## Why
Currently, the GM Dashboard only displays global counts (`fakeTraders`, `fakeHunters`) on the status card. Server admins and GMs cannot inspect individual bot behaviors, map coordinates (X, Y, Z), current combat targets, health/mana levels, or DNA profiles.

Adding a dedicated **Fake Players Live Monitor** tab in the GM Control Panel will provide complete real-time visibility into every bot operating on the server, enable spatial analysis of bot distribution across zones, and grant GMs direct control actions (such as teleporting to a bot, despawning stuck bots, or forcing safety retreats).

## What Changes
1. **Backend REST Endpoints**:
   - `GET /api/admin/fakeplayers/list`: Returns detailed JSON array of all online Fake Hunters & Fake Traders (`name`, `type`, `level`, `class`, `x`, `y`, `z`, `zone`, `hpPercent`, `mpPercent`, `state`, `targetName`, `dnaArchetype`).
   - `POST /api/admin/fakeplayers/action`: Allows GMs to execute direct actions (`TELEPORT_GM`, `DESPAWN`, `FORCE_RETREAT`, `RESPAWN`).
2. **Frontend GM Dashboard Tab (`🤖 FakePlayers Monitor`)**:
   - Added as a protected tab in the GM Control Panel.
   - Summary metric cards (Total Bots, Active Hunters, Active Traders, In-Combat %, Safety Fleeing).
   - Filterable data table by Bot Type, Status, and Zone.
   - Live HP/MP progress bars, coordinate links, and quick GM action buttons.

## Impacted Components
- `com.l2journey.gameserver.managers.WebAPIManager`
- `web/index.html`

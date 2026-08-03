# Technical Design: Hierarchical Waypoints & Behavior Profile Engine

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       XML Data Parsers & Storage                            │
├──────────────────────────────────────┬──────────────────────────────────────┤
│  `fake_hunter_profiles.xml`          │  `fake_hunter_waypoints.xml`         │
│  • townReturnDelay                   │  • Town Routes (Vila -> GK)          │
│  • allowKS (true/false)              │  • Transit Routes (Teleport -> Spot) │
│  • pickupItems (true/false)          │  • Zone Patrol Routes (Spot Patrol)  │
│  • kitingDistance                    │  • Jitter Variation Radius (ex: 100u)│
└──────────────────────────────────────┴──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    FakeHunter Navigation State Machine                      │
├─────────────────────────────────────────────────────────────────────────────┤
│ 1. TOWN_NAV    ──▶ Follow Town Waypoints with Jitter to Gatekeeper         │
│ 2. TELEPORT    ──▶ Trigger Gatekeeper Teleport to Hunt Zone                 │
│ 3. TRANSIT     ──▶ Follow Transit Waypoints to Hunting Sub-Zone            │
│ 4. PATROL/HUNT ──▶ Patrol Hunt Waypoints, Attack Mobs, Pickup Drops        │
│ 5. RECOVER/REV ──▶ On Death, Wait `townReturnDelay`, Respawn & Restart Loop │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Technical Decisions

### 1. Behavior Profile Configuration Schema (`fake_hunter_profiles.xml`)
- Each profile defines:
  ```xml
  <profile id="cautious_hunter">
      <townReturnDelay>30</townReturnDelay> <!-- Seconds to wait before returning to town on death -->
      <allowKS>false</allowKS>             <!-- Avoid attacking mobs engaged by human players -->
      <pickupItems>true</pickupItems>       <!-- Auto-pickup nearby drops -->
      <groupAssist>true</groupAssist>       <!-- Assist party members in combat -->
      <kitingDistance>300</kitingDistance>  <!-- Distance to retreat if mob gets too close -->
      <assignedRoutes>
          <route>gludio_town_to_gk_path_a</route>
          <route>despair_teleport_to_south_camp</route>
          <route>despair_south_camp_patrol</route>
      </assignedRoutes>
  </profile>
  ```

### 2. Hierarchical Waypoint Schema (`fake_hunter_waypoints.xml`)
- Waypoints are categorized into 3 levels:
  1. **TOWN_WAYPOINTS**: Multiple pathways inside cities (e.g. Gludio Path A, Path B) leading to the GK.
  2. **TRANSIT_WAYPOINTS**: Paths connecting arrival teleport nodes to specific hunting camps.
  3. **ZONE_WAYPOINTS**: Loop patrol nodes within hunting sub-zones.
- Node Definition with Jitter Variation:
  ```xml
  <route id="gludio_town_to_gk_path_a" type="TOWN">
      <waypoint x="-14347" y="123622" z="-3120" jitter="100" />
      <waypoint x="-14500" y="123700" z="-3120" jitter="80" />
      <waypoint x="-14780" y="123800" z="-3120" jitter="50" />
  </route>
  ```

### 3. Jitter Navigation Algorithm
- When moving to a waypoint `(wpX, wpY, wpZ)` with `jitter`:
  - Calculate target coordinate:
    - `targetX = wpX + Rnd.get(-jitter, jitter)`
    - `targetY = wpY + Rnd.get(-jitter, jitter)`
  - Prevents bots from marching on identical single pixels, producing natural human movement.

### 4. Target Selection & Looting Pipeline
- **Looting**: If `pickupItems` is `true` and ground items exist within 500u, execute `Intention.PICK_UP`.
- **Anti-KS Filter**: If `allowKS` is `false`, check if candidate mob is currently in combat with a human player.

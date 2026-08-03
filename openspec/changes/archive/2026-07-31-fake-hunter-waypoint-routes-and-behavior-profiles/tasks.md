# Tasks: Hierarchical Waypoints & Behavior Profile Engine

## 1. Profile & Waypoint Parsers & Data Structures
- [x] **1.1 Create `fake_hunter_profiles.xml` and Parser Data Models** <!-- id: 0 -->
  - Parse behavior parameters (`townReturnDelay`, `allowKS`, `pickupItems`, `groupAssist`, `kitingDistance`).
- [x] **1.2 Create `fake_hunter_waypoints.xml` and Waypoint Route Registry** <!-- id: 1 -->
  - Parse hierarchical routes (`TOWN`, `TRANSIT`, `ZONE`, `EXCLUSIVE`) with node coordinates and jitter variation radius (`jitter`).

## 2. Navigation State Machine & Jitter Movement
- [x] **2.2 Implement Waypoint Navigation with Jitter Randomization** <!-- id: 2 -->
  - Calculate randomized offsets `(wpX ± jitter, wpY ± jitter)` for natural walking paths.
- [x] **2.3 Implement Town-to-GK and Transit-to-Zone Route Execution** <!-- id: 3 -->
  - Guide hunters through town paths to Gatekeeper and transit paths to hunting camps.

## 3. Looting, Target Selection & Respawn Cycle
- [x] **3.1 Implement Auto-Loot Pickup Engine (`Intention.PICK_UP`)** <!-- id: 4 -->
  - Scan ground items within 500u and move to pick up drops after combat.
- [x] **3.2 Implement Anti-KS & Assist Target Selector** <!-- id: 5 -->
  - Enforce `allowKS` rules to prevent attacking mobs engaged by human players, and prioritize party assist.
- [x] **3.3 Implement Death Respawn & Return-to-Town Loop** <!-- id: 6 -->
  - Handle `townReturnDelay`, auto-revive on death, and restart the town-to-zone navigation loop.

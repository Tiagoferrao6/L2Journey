# Proposal: FakeHunter Hierarchical Waypoints, Route Navigation & Behavior Profiles

## Why

1. **Robotic Movement & Single Line Walking**: FakeHunters currently move in a straight line toward hardcoded coordinates, making their movement artificial and predictable.
2. **Lack of Route Hierarchy**: There are no structured waypoints for navigating towns to reach Gatekeepers, traveling from teleport spawn points to hunting spots, or patrolling within sub-zones.
3. **Hardcoded AI Behavior**: Behavior settings such as death return delays, Kill-Stealing (`allowKS`), item looting (`pickupItems`), and assist rules are hardcoded instead of being dynamically configured per behavior profile.

## What Changes

- **XML Behavior Profiles (`fake_hunter_profiles.xml`)**:
  - Define customizable behavior parameters: `townReturnDelay`, `allowKS`, `pickupItems`, `groupAssist`, `kitingDistance`, `preferredZones`.
- **Hierarchical Waypoint System (`fake_hunter_waypoints.xml`)**:
  - **Town Navigation Routes**: General paths in towns leading to Gatekeepers, with configurable path selection and coordinate jitter variation (e.g., ±100 units).
  - **Transit Routes**: Paths from teleport arrival points to hunting sub-zones.
  - **Zone Patrol Routes**: Sub-zone patrol paths for active hunting and monster searching.
  - **Exclusive Routes**: Specific waypoints assigned to individual hunters or archetypes.
- **Hunter AI Navigation Engine**:
  - Implement waypoint-following logic with randomized radius offsets (jitter) to prevent bots from walking in identical single lines.
  - Implement auto-loot pickup for dropped items (`Intention.PICK_UP`).
  - Implement target selector with anti-KS and party assist prioritization.
  - Implement automatic death revival and return-to-town cycle.

## Impacted Components

- `com.l2journey.gameserver.managers.FakeHunterManager`
- `com.l2journey.gameserver.model.actor.dna.HunterDNA`
- `com.l2journey.gameserver.data.xml.impl.FakeHuntersDNAParser`
- `com.l2journey.gameserver.model.actor.instance.FakePlayer`
- `config/npcs/fake_hunter_profiles.xml`
- `config/npcs/fake_hunter_waypoints.xml`

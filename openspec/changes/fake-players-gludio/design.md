## Context

The current static bot system in Gludio is rigid, predictable, and doesn't simulate real player behavior well. We want to implement a more dynamic system where "fake players" have persistent profiles (DNA), economic cycles, and behaviors (such as trading or hunting) that react to real players.

## Goals / Non-Goals

**Goals:**
- Create a data schema for persisting fake player profiles and states (`fake_players_profiles`).
- Implement `FakePlayerManager` to handle the lifecycle (spawn/despawn) of fake players based on their schedules and the presence of real players in the zone.
- Implement an AI Controller for Traders with cyclic inventory management.
- Implement an AI Controller for Hunters with party logic and combat reactivity based on "DNA".
- Keep parameters externalized (config/DB).

**Non-Goals:**
- Expand this system to other towns/regions in this initial change (limited to 30 Traders and 30 Hunters in Gludio).
- Full market simulation with advanced supply/demand across all towns.
- Implementing complex clan logic for fake players.

## Decisions

- **Persistence Layer**: Use a new SQL table `fake_players_profiles`. This table will store everything needed to recreate the bot's state across server restarts, including their schedule, location, and DNA traits (aggressiveness, courage, party tendency).
- **Zone-Based Activation**: To optimize performance, `FakePlayerManager` will use a Zone Listener to only spawn and activate bots when a real player is in the Gludio region. When no players are around, the bots are despawned but their time-based logic (like economic cycles or schedules) can still be calculated upon next spawn.
- **AI Implementation**: Extend the existing AI controllers to create specialized `FakeTraderAI` and `FakeHunterAI`. 
  - *TraderAI* will periodically refresh its sell slots and update prices based on a simple configuration matrix.
  - *HunterAI* will use the DNA weights to make combat decisions (e.g., if courage < threshold and HP is low, trigger escape).

## Risks / Trade-offs

- **Risk: Performance impact from Zone Listeners** -> Mitigation: Use a broad bounding box for Gludio to minimize enter/exit events.
- **Risk: Complex state sync when despawning/respawning** -> Mitigation: Ensure `FakePlayerManager` strictly saves state to DB upon despawn, and re-reads upon spawn.

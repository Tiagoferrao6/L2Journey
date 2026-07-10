## Why

The current static bot system lacks dynamism and realism. By replacing it with persistent agents that possess behavioral profiles (DNA), economic cycles, and daily routines, we can create a more vibrant and immersive world. This initial scope focuses on the Gludio region (30 Traders and 30 Hunters) to validate the new ecosystem mechanics.

## What Changes

- **Data Persistence**: Introduction of a `fake_players_profiles` SQL table to store identification, behavioral DNA (aggressiveness, courage, party tendency), daily schedules (4 to 8 hours), and current states (inventory, home-zone, party status).
- **Trader Intelligence**: Traders will have 1-5 sales slots with fixed or variable items. An economic cycle will renew inventories every 24h to 7 days, adjusting prices based on availability.
- **Hunter Intelligence**: Hunters will follow behavior profiles (e.g., Tankers being more aggressive/courageous). They will feature party logic, reactivity (attacking, provoking, or escaping to town when HP/MP is low), and optimization (activating only when a real player is in the zone).
- **External Configuration**: All parameters (weights, schedules, item lists) will be externalized for easy adjustments.

## Capabilities

### New Capabilities
- `fake-player-persistence`: Database schemas and data access layer for persistent fake player profiles.
- `fake-player-ai-trader`: AI logic and economic cycles for fake traders.
- `fake-player-ai-hunter`: Behavioral DNA, party logic, and combat reactivity for fake hunters.
- `fake-player-manager`: Core system for spawning, despawning, and managing fake players based on zone influence and schedules.

### Modified Capabilities


## Impact

- **Database**: Adds a new table `fake_players_profiles`.
- **AI/Controllers**: Introduces new AI controllers for Traders and Hunters, and a new `FakePlayerManager`.
- **Zone Listeners**: Adds logic to detect real players in Gludio to optimize fake player activation.
- **Server Performance**: Might slightly increase DB reads/writes and memory due to persistent states and zone listeners, but the optimization to only activate bots when players are near should mitigate CPU impact.

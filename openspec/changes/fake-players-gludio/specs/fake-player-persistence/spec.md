## ADDED Requirements

### Requirement: Database Schema for Profiles
The system SHALL have a SQL table named `fake_players_profiles` to store persistent data for fake players.

#### Scenario: Server Startup
- **WHEN** the server starts
- **THEN** it validates that the `fake_players_profiles` table exists and matches the required schema, containing fields for ID, DNA weights, schedule, and state.

### Requirement: State Loading
The system SHALL load a bot's state from `fake_players_profiles` when preparing to spawn a fake player.

#### Scenario: Spawning a Fake Player
- **WHEN** `FakePlayerManager` decides to spawn a bot
- **THEN** the system queries `fake_players_profiles` to retrieve its DNA, inventory, location, and party status.

### Requirement: State Saving
The system SHALL save a bot's current state to `fake_players_profiles` upon despawn.

#### Scenario: Despawning a Fake Player
- **WHEN** `FakePlayerManager` despawns a bot (e.g., due to its schedule ending or no real players nearby)
- **THEN** the system updates the database with its current location, inventory, and party status.

## ADDED Requirements

### Requirement: Zone-Based Activation Listener
The system SHALL monitor the presence of real players within the Gludio region using a Zone Listener.

#### Scenario: Real Player Enters Gludio
- **WHEN** a real player enters the Gludio bounding box
- **THEN** the `FakePlayerManager` is notified to wake up and start processing fake player logic.

### Requirement: Conditional Spawning
The `FakePlayerManager` SHALL only spawn and activate fake players in Gludio if there is at least one real player in the region.

#### Scenario: Spawning Bots on Player Proximity
- **WHEN** the `FakePlayerManager` detects real player presence
- **THEN** it iterates through the active schedules of Gludio fake players (up to 30 Traders and 30 Hunters) and spawns those that should currently be active.

### Requirement: Schedule Management
The `FakePlayerManager` SHALL ensure fake players respect their configured daily schedules (e.g., active for 4 to 8 hours).

#### Scenario: Bot Schedule Ends
- **WHEN** a fake player's daily schedule duration expires
- **THEN** the `FakePlayerManager` despawns the bot, triggers the state save to the database, and marks it as inactive until its next cycle.

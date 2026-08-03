## MODIFIED Requirements

### Requirement: XML-Based Configuration & Persistence (Ghost Objects)
The system SHALL use XML configurations (`fake_traders_spawns.xml`, `fake_traders_economy.xml`, `fake_hunters_spawns.xml`) instead of SQL database tables to manage spawns, inventory templates, and bot parameters.

#### Scenario: Server Startup
- **WHEN** the server starts
- **THEN** it parses the XML configuration files to register spawn locations, trade profiles, and hunter parameters without requiring database queries.

### Requirement: In-Memory Lifecycle & Despawn
The system SHALL manage bot states (location, target, trade status) in memory (Ghost Objects) during active runtime.

#### Scenario: Despawning or Zone Sleep
- **WHEN** `FakePlayerManager` suspends or despawns a bot (e.g., due to no real players nearby)
- **THEN** it safely removes the in-memory ghost instance and releases resources without requiring database write operations.

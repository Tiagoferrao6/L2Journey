# geodata-realtime-pathfinding Specification

## Requirements

### Requirement: Realtime Geodata Pathfinding Configuration
The GameServer MUST load Geodata `.l2j` map files from `./data/geodata` and execute A* realtime pathfinding (`PathFinding = 2`) for movement and collision detection.

#### Scenario: Geodata Initialization
- **GIVEN** Geodata files are present in `./data/geodata`
- **WHEN** GameServer starts up
- **THEN** Geodata engine initializes with `PathFinding = 2` and `CoordSynchronize = 2`.

#### Scenario: Obstacle Avoidance Movement
- **GIVEN** A FakePlayer is in Gludio town center
- **WHEN** Movement to Gatekeeper location is issued
- **THEN** Pathfinding calculates a route around buildings without clipping through walls.

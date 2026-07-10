## ADDED Requirements

### Requirement: Trader AI Initialization
The system SHALL initialize a Trader AI for any fake player designated as a Trader in Gludio.

#### Scenario: Trader Bot Spawns
- **WHEN** a Trader bot is spawned by the FakePlayerManager
- **THEN** it sets up a private store based on its loaded inventory and pricing strategy.

### Requirement: Economic Cycles
The Trader AI SHALL refresh its inventory and prices at configurable intervals (24h to 7 days).

#### Scenario: Inventory Refresh Cycle
- **WHEN** the economic cycle timer expires for a Trader
- **THEN** the Trader generates new items for its 1-5 sales slots based on Gludio's configured loot table and adjusts prices based on availability.

### Requirement: Private Store Management
The Trader AI SHALL keep its private store open and interact correctly with real players who purchase items.

#### Scenario: Real Player Purchases Item
- **WHEN** a real player buys an item from the Trader's private store
- **THEN** the Trader AI processes the transaction, updates its inventory, and closes the store if it runs out of items.

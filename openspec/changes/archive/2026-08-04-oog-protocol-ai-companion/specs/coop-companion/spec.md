# Coop Companion Spec - Delta Spec for OOG Protocol & Dual Control

## ADDED Requirements

### Requirement: Out-Of-Game Protocol Emulation

The system SHALL allow AI companions to authenticate, create characters, and operate using real database accounts via protocol emulation without direct SQL mutations.

#### Scenario: Autonomous Character Creation via OOG Protocol
- **GIVEN** an empty user account allocated for AI companion execution
- **WHEN** the OOG protocol driver authenticates the account
- **THEN** it SHALL execute character creation specifying name, classId, and visual appearance without direct database inserts.

#### Scenario: Real Account Dual Control Handover
- **GIVEN** an AI companion active in OOG mode on a database character
- **WHEN** a human player logs in to the same account via the official L2 client
- **THEN** the server SHALL gracefully disconnect the OOG session and transfer character control to the human player.

#### Scenario: Protocol-Enforced Progression Rules
- **GIVEN** an AI companion performing actions in OOG mode
- **WHEN** it attempts movement, attack, loot pickup, or merchant purchases
- **THEN** all actions SHALL be validated by standard GameServer packet handlers, distance checks, and inventory limits.

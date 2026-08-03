# fake-player-behavior-tree Specification

## Requirements

### Requirement: Behavior Tree Decision Engine for FakePlayer
The FakePlayer AI MUST execute decisions using a modular Behavior Tree (BT) that handles state evaluation, urban navigation, and NPC dialog bypass execution.

#### Scenario: Single Bot Test Spawn
- **GIVEN** Single test bot mode is enabled
- **WHEN** FakeHunterManager initializes
- **THEN** Exactly 1 test bot (`TestBot`) is spawned in Gludio town center.

#### Scenario: NPC Dialog & Teleport Bypass Interaction
- **GIVEN** TestBot arrives at Gatekeeper NPC
- **WHEN** BTActionInteractBypass node executes
- **THEN** TestBot targets the NPC, triggers `onAction`, and sends `onBypassFeedback` to execute the teleport dialog like a real player.

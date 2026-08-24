# badge-npc-weapon-exchange Specification

## Purpose
TBD - created by archiving change add-royal-weapons. Update Purpose after archive.
## Requirements
### Requirement: Exclusive Conqueror's Badge Weapon Exchange
The game server SHALL provide custom multisell XML entries that restrict the acquisition of Royal Weapons exclusively to NPC exchange using Conqueror's Badges (Item ID 99000).

#### Scenario: Exchanging Conqueror Badges for Royal Weapon
- **WHEN** a player interacts with the custom NPC exchange shop and provides the required amount of Conqueror's Badges (Item ID 99000)
- **THEN** the server removes the badges from player inventory and grants the selected Royal Weapon

#### Scenario: Insufficient Conqueror Badges
- **WHEN** a player attempts to purchase a Royal Weapon without enough Conqueror's Badges
- **THEN** the transaction fails and no items are exchanged


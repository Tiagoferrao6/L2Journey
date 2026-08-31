## ADDED Requirements

### Requirement: Golkonda Multiverse Encounters
The system SHALL provide three tiers of Golkonda Raid Bosses, each with a different chance to drop the "Golkonda Horn" item (ID 99900).

#### Scenario: Tier 1 Exiled Golkonda Defeat
- **WHEN** a player defeats Exiled Golkonda (ID 29000) in The Cemetery
- **THEN** there is a 25% chance to drop 1 Golkonda Horn

#### Scenario: Tier 2 Original Golkonda Defeat
- **WHEN** a player defeats Golkonda (ID 25126) in Tower of Insolence
- **THEN** there is a 50% chance to drop 1 Golkonda Horn

#### Scenario: Tier 3 Infernal Golkonda Defeat
- **WHEN** a party defeats Infernal Golkonda (ID 29001) in Monastery of Silence
- **THEN** the boss drops 1 to 5 Golkonda Horns with 100% chance

### Requirement: Race Restriction on Cumulative Subclass
The system SHALL strictly enforce that the selected Cumulative Subclass shares the exact same race as the character's currently active class slot.

#### Scenario: Human selecting a Cumulative Class
- **WHEN** a Human Paladin attempts to add a Cumulative Subclass
- **THEN** only Human classes (e.g., Warlord, Sorcerer) are presented as options

#### Scenario: Attempting to bypass Race Restriction
- **WHEN** a Dark Elf Phantom Ranger attempts to select an Elf Silver Ranger as cumulative subclass
- **THEN** the system rejects the selection and displays an error message

### Requirement: Dual Class Application
The system SHALL apply the selected Cumulative Subclass to the character's active class slot by consuming 1 Golkonda Horn and writing the selection to the database.

#### Scenario: Successful application
- **WHEN** a player holding a Golkonda Horn selects a valid cumulative subclass at the NPC
- **THEN** the system consumes the Horn, sets the `dual_class_id` for the current class index in `character_subclasses`, and instructs the player to relog to apply the skills

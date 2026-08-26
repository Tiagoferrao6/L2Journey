# fake-shops-gludio Specification

## ADDED Requirements

### Requirement: Always-Active Execution Mode
The system SHALL support an explicit configuration flag `FakePlayerAlwaysActive` in `fakeplayers.ini` that allows FakeShops to remain active in the game world 24 hours a day, 7 days a week, ignoring the presence or absence of real players in the zone.

#### Scenario: Server starts with Always-Active mode enabled
- **WHEN** `FakePlayerAlwaysActive` is set to `True` in `fakeplayers.ini`
- **THEN** `FakePlayerManager` SHALL spawn all configured FakeShops in Gludio upon server startup regardless of real player count in the Gludio town zone

### Requirement: Configurable Time Window for Bot Shifts
The system SHALL allow server administrators to configure specific start and end hours (`FakePlayerShiftStartHour` and `FakePlayerShiftEndHour`) in 24-hour format (HH:mm) based on the system local time zone (`America/Sao_Paulo` / UTC-3 Brasilia time).

#### Scenario: Time window check during schedule evaluation
- **WHEN** the periodic schedule evaluation runs every 60 seconds
- **THEN** the system SHALL check current Brasilia local time against `FakePlayerShiftStartHour` and `FakePlayerShiftEndHour` and trigger spawn or despawn accordingly

### Requirement: Fixed 15 FakeShops with Database Account Binding
The system SHALL maintain a fixed list of 15 unique FakeShop profiles in Gludio. Each FakeShop SHALL be linked to a registered account in the database (`accounts` and `characters` tables) and enforce single-session login rules to prevent duplicate logins.

#### Scenario: FakeShop authentication and spawn
- **WHEN** a FakeShop attempts to spawn
- **THEN** the system SHALL verify its account credentials in the database, acquire the character instance, set its online status, and prevent any secondary login attempts for that account

### Requirement: Exclusive Top D-Grade Catalog for Gludio FakeShops
The system SHALL expand `city_catalogs.xml` and `fake_shops.xml` to ensure that all sales offerings across equipment, weapons, armor sets, jewelry, consumables, shots, enchants, and recipes are strictly composed of **Top D-Grade** tier items (e.g., Brigandine Set, Manticore Set, Knowledge Robe Set, Elven Jewelry, Elven Long Sword, Mithril Dagger, Staff of Life, Bone Arrows, Enchant Scroll D).

#### Scenario: Player interacts with a Top D-Grade FakeShop
- **WHEN** a real player opens a FakeShop store in Gludio
- **THEN** the store SHALL present exclusively Top D-Grade items (SELL/BUY/CRAFT) with randomized prices within min/max bounds and valid quantities

#### Scenario: 24-hour Economic Cycle Refresh
- **WHEN** the 24-hour economic update interval elapses for a FakeShop
- **THEN** the FakeShop SHALL restock its inventory with Top D-Grade catalog items, re-randomize item quantities and prices within catalog parameters, and refresh its store status

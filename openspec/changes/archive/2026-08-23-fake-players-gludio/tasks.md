## 1. Database and Persistence Setup

- [x] 1.1 Create SQL script for `fake_players_profiles` table
- [x] 1.2 Implement Data Access Object (DAO) for `fake_players_profiles`
- [x] 1.3 Add logic to save/load bot state based on ID

## 2. Core Manager Implementation

- [x] 2.1 Create `FakePlayerManager` class with basic singleton structure
- [x] 2.2 Implement schedule and cycle management logic within `FakePlayerManager`
- [x] 2.3 Implement Zone Listener for Gludio region to detect real player presence
- [x] 2.4 Add conditional spawning/despawning logic based on Zone Listener events

## 3. Fake Trader AI Implementation

- [x] 3.1 Create `FakeTraderAI` class extending base AI/Controller
- [x] 3.2 Implement inventory generation logic based on Gludio loot/items
- [x] 3.3 Implement private store initialization and price setting
- [x] 3.4 Implement economic cycle refresh logic for Traders

## 4. Fake Hunter AI Implementation

- [x] 4.1 Create `FakeHunterAI` class extending base AI/Controller
- [x] 4.2 Implement DNA parsing and behavioral mapping (Aggressiveness, Courage, Party Tendency)
- [x] 4.3 Implement party logic to allow Hunters to form groups and follow a leader
- [x] 4.4 Implement combat reactivity (attack, provoke, flee/escape on low HP)

## 5. Integration and Configuration

- [x] 5.1 Externalize configuration parameters (XML/Properties) for schedules, DNA bounds, and Gludio item tables
- [x] 5.2 Integrate `FakePlayerManager` initialization into the main Server startup sequence
- [x] 5.3 Test full lifecycle: server start, player enters Gludio, bots spawn, player leaves, bots despawn

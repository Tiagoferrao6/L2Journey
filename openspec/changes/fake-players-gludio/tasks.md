## 1. Database and Persistence Setup

- [ ] 1.1 Create SQL script for `fake_players_profiles` table
- [ ] 1.2 Implement Data Access Object (DAO) for `fake_players_profiles`
- [ ] 1.3 Add logic to save/load bot state based on ID

## 2. Core Manager Implementation

- [ ] 2.1 Create `FakePlayerManager` class with basic singleton structure
- [ ] 2.2 Implement schedule and cycle management logic within `FakePlayerManager`
- [ ] 2.3 Implement Zone Listener for Gludio region to detect real player presence
- [ ] 2.4 Add conditional spawning/despawning logic based on Zone Listener events

## 3. Fake Trader AI Implementation

- [ ] 3.1 Create `FakeTraderAI` class extending base AI/Controller
- [ ] 3.2 Implement inventory generation logic based on Gludio loot/items
- [ ] 3.3 Implement private store initialization and price setting
- [ ] 3.4 Implement economic cycle refresh logic for Traders

## 4. Fake Hunter AI Implementation

- [ ] 4.1 Create `FakeHunterAI` class extending base AI/Controller
- [ ] 4.2 Implement DNA parsing and behavioral mapping (Aggressiveness, Courage, Party Tendency)
- [ ] 4.3 Implement party logic to allow Hunters to form groups and follow a leader
- [ ] 4.4 Implement combat reactivity (attack, provoke, flee/escape on low HP)

## 5. Integration and Configuration

- [ ] 5.1 Externalize configuration parameters (XML/Properties) for schedules, DNA bounds, and Gludio item tables
- [ ] 5.2 Integrate `FakePlayerManager` initialization into the main Server startup sequence
- [ ] 5.3 Test full lifecycle: server start, player enters Gludio, bots spawn, player leaves, bots despawn

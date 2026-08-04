# Tasks: Out-Of-Game (OOG) Protocol Driver & Real Account Dual Control

- [x] 1. Implement OOG Client Session Engine (`OOGClientSession`) <!-- id: 1 -->
  - [x] 1.1 Create `OOGClientSession.java` for managing virtual network session states.
  - [x] 1.2 Implement login authentication and character selection protocol handling.

- [x] 2. Implement Autonomous Character Creation Agent (`OOGCharacterCreator`) <!-- id: 2 -->
  - [x] 2.1 Create `OOGCharacterCreator.java` for empty account detection.
  - [x] 2.2 Implement character creation request generation specifying race, class, and cosmetics.

- [x] 3. Implement Human Dual-Control Handover System <!-- id: 3 -->
  - [x] 3.1 Implement concurrent login detection in `GameClient` to gracefully disconnect OOG sessions.
  - [x] 3.2 Implement auto-reconnection of OOG sessions upon human logout.

- [x] 4. System Integration & Multi-Account Raid Squad Validation <!-- id: 4 -->
  - [x] 4.1 Update `LLMCompanionManager` to use `OOGClientSession` for persistent characters.
  - [x] 4.2 Test end-to-end OOG character creation, leveling, and human handover flow.

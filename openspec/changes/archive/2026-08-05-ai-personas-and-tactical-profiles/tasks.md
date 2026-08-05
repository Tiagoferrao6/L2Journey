# Tasks: AI Personas & Tactical Combat Profiles

- [x] 1. Implement AI Persona Profile Engine (`AIPersonaProfileManager`) <!-- id: 1 -->
  - [x] 1.1 Create `AIPersonaProfileManager.java` and `AIPersonaProfile.java` model classes.
  - [x] 1.2 Define initial persona profiles for Crystal, Esquizitinha, and Shirou.

- [x] 2. Implement Crystal Tactical Combat Engine (Silver Ranger) <!-- id: 2 -->
  - [x] 2.1 Implement max range positioning, kiting vectors, and low HP target selection.
  - [x] 2.2 Implement KS reaction (Stunning Shot) and PvP Hit&Run with Entangle and Escape Scroll at <30% HP.

- [x] 3. Implement Esquizitinha Tactical Healing Engine (Bishop) <!-- id: 3 -->
  - [x] 3.1 Implement Frenzy/Zealot Limiter protection (Balance Life suppression & post-buff Major Heal).
  - [x] 3.2 Implement Trance aggro control, Cleanse priority queue, LoS cover positioning, and Celestial Shield clutch save.

- [x] 4. Implement Shirou Tactical Frontline Engine (Warlord / Paladin) <!-- id: 4 -->
  - [x] 4.1 Implement Warlord mob training (5-10 mobs), Howl debuff, and AoE skill execution.
  - [x] 4.2 Implement Paladin Aggression, Angelic Icon, Shock Stomp KS reaction, and Ultimate Defense / Sacrifice protection.

- [x] 5. System Integration & End-to-End Persona Validation <!-- id: 5 -->
  - [x] 5.1 Connect persona profiles to `LLMCompanionManager` and OOG sessions.
  - [x] 5.2 Create unit test `LLMAPersonasTest.java` and validate build compilation.

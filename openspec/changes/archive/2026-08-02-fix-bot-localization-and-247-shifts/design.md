# Design Document: Fix Bot Localization & 24/7 FakeHunters Active Mode

## Context
When `MULTILANG_ENABLE` is set to `true`, damage notifications call `NpcNameLocalisationData.getInstance().getLocalisation(_lang, target.getId())`.
`FakePlayer` entities have `_lang = null` by default, causing `ConcurrentHashMap.get(null)` in `NpcNameLocalisationData` to throw a `NullPointerException`.

Furthermore, `FakeHunterManager` runs a `ShiftTick` that despawns bots when their configured shift (`MORNING`, `PRIME_TIME`, etc.) expires. For observation and behavior analysis, all FakeHunters should remain active 24/7.

## Goals / Non-Goals

**Goals:**
- Eliminate `NullPointerException` when localized NPC names are requested for non-client `FakePlayer` bots.
- Set default language `"en"` for all `FakePlayer` instances.
- Ensure `NpcNameLocalisationData.getLocalisation(lang, id)` is null-safe when passed a `null` language parameter.
- Keep all FakeHunters active 24/7 without despawning during shift transitions in `ShiftTick`.

**Non-Goals:**
- Implementing full multilingual translation files for all bot types.
- Modifying human player client language selection.

## Decisions

### Decision 1: Null-Safe Language Lookup & Default "en" for FakePlayers
- In `NpcNameLocalisationData.getLocalisation`:
  ```java
  if (lang == null)
  {
      return null;
  }
  ```
- In `FakePlayer.java` constructor:
  ```java
  setLang("en");
  ```
  This ensures that damage calculation and localized text lookup always receive a valid language string `"en"`.

### Decision 2: 24/7 Operation for FakeHunters
- In `FakeHunterManager.java` (`ShiftTick`):
  Bypass despawning by returning early or ignoring shift expiration checks for all FakeHunter bots:
  ```java
  // All FakeHunters remain active 24/7 for behavioral analysis
  continue;
  ```

## Risks / Trade-offs
- **[Risk]** Slightly higher server CPU/memory usage with all FakeHunters active 24/7.
  - **Mitigation:** Regional sleep mode (`GLUDIO` sleep state when no human players are nearby) remains intact to optimize server resources when no players are in range.

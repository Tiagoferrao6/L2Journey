# Proposal: Fix Bot Localization NullPointerException & Set All FakeHunters to 24/7 Active

## Why
When `FakePlayer` (bot) characters attack monsters with multi-language enabled (`Config.MULTILANG_ENABLE = true`), `Player.sendDamageMessage()` attempts to retrieve NPC localized names using the character's `_lang`. Because `FakePlayer` instances do not have a network client or selected language (`_lang == null`), `NpcNameLocalisationData.getLocalisation(null, id)` throws a `NullPointerException` on `ConcurrentHashMap.get(null)`. This crashes the scheduled `HitTask` thread pool.

Additionally, to thoroughly observe FakeHunter combat behaviors, pathing, and AI routines without interruption, all FakeHunters need to operate 24/7 without despawning during shift schedule transitions.

## What Changes
- **NPC Localization Default Language & Null Safety**:
  - In `NpcNameLocalisationData.getLocalisation(lang, id)`: Return `null` safely if `lang` is `null` to prevent `NullPointerException`.
  - In `FakePlayer.java`: Initialize default language `_lang = "en"` so bot localization queries default to English.
- **24/7 Active Mode for All FakeHunters**:
  - In `FakeHunterManager.java` (`ShiftTick`): Exempt all FakeHunters from shift despawning (or treat their shift as `ALL_DAY`) so all bots remain continuously active in-game 24/7.

## Capabilities

### New Capabilities
- `fake-player-language-fallback`: Default language assignment (`"en"`) and null-safe NPC localization lookups for non-client `FakePlayer` entities.
- `fake-hunter-continuous-shift`: 24/7 continuous operation mode for all FakeHunter bots across all regions.

### Modified Capabilities
- None

## Impact
- **Backend / GameServer**:
  - `com.l2journey.gameserver.data.xml.NpcNameLocalisationData`: Null guard on `getLocalisation`.
  - `com.l2journey.gameserver.model.actor.instance.FakePlayer`: Default language initialization (`"en"`).
  - `com.l2journey.gameserver.managers.FakeHunterManager`: `ShiftTick` bypass for 24/7 bot operation.

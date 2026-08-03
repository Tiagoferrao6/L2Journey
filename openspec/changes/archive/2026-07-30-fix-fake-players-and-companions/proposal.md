# Proposal: Fix & Enhance FakePlayers (Traders, Hunters), Mercenary Companions & Dashboard Live Chat Location Tracking

## Why
Currently, FakeHunters and Mercenary Companions spawn unequipped (naked in basic starter clothing), making them visually incomplete and combat-ineffective. Furthermore:
1. Mercenary Companions lack automated dual shot usage (Soulshot + Blessed Spiritshot), auto-teleporting alongside the player, party-wide healing/buffing, and level-scaled skill catalogues.
2. Players lack an interactive management screen to control their companion's behavior, stances, buffs, and recall actions.
3. FakeHunters often stay idle in towns instead of walking naturally to the Gatekeeper NPC to teleport out.
4. FakePlayers (Traders, Hunters, Companions) lack a structured grade-based gearing, dual-shot usage, health potion consumption, and faithful field AI (PvE & PvP).
5. In the GM Dashboard Live Chat Monitor, GMs cannot see where a player was when speaking (missing X, Y, Z coordinates and Region name) and cannot filter chat logs by specific map regions.
6. GMs lack direct control in the Web Dashboard to view and edit individual FakePlayer locations, activate/deactivate bots, change AI behaviors, or modify bot levels and equipment grade tiers live.

## Model Choice & Mechanics Architecture
We evaluated Lineage 2 companion mechanics:
- **Pet (`L2PetInstance`)**: Uses native pet control window, but requires food/hunger management and limits human paperdoll armor visual rendering.
- **Agathion (`AgathionInstance`)**: Purely cosmetic visual floating on shoulder; cannot take damage, cannot heal party members, has no stats.
- **Summon (`L2Summon`)**: Servitor controlled by crystals/time; uses pet window but occupies summon slot and lacks full player armor visuals.
- **Model B (RECOMMENDED - Hybrid `MercenaryInstance` + Party Member + Auto-Teleport + Companion Control UI)**:
  - Keeps the companion as a full human-like player character in Party (`Party.addPartyMember(merc)`).
  - Being in Party allows the Companion to monitor **all party members' HP/MP** and apply heals/buffs party-wide.
  - Can wear top S/A/B/C/D Grade player armor sets, dual weapons, robes, and jewels with glow effects.
  - Adds **Instant Auto-Teleport** whenever the master teleports.
  - Provides an in-game HTML Management Screen (accessible via `.companion`, Alt+B, or clicking the Mercenary Contract item).

## What Changes

### 1. Dual Shot Injection (Soulshot + Blessed Spiritshot)
- Enable **Dual Shot Auto-Charge**: FakePlayers and Mercenary Companions automatically charge BOTH Soulshots (for physical attacks/skills) AND Blessed Spiritshots (for magic skills, buffs, and heals) simultaneously, matched to their equipped weapon grade without consuming inventory items.

### 2. Realistic Town Gatekeeper Walking Dispatch
- When a `FakeHunter` spawns in or returns to a town, it will no longer vanish instantly from the town square.
- The AI will execute a natural walk (`moveToLocation`) to the nearest Gatekeeper NPC (e.g. GK Bella in Gludio).
- Upon reaching the Gatekeeper (within ~100 radius), it triggers the Gatekeeper teleport animation/effect and teleports to its designated hunting ground spawn point, transitioning from `IDLE` to `HUNTING`.

### 3. Grade-Based Auto-Equip Engine (No-Grade to S-Grade)
- Every `FakePlayer` (Trader, Hunter, Companion) automatically receives and equips top-grade Weapon, Armor Set, and Jewels matched to its class role and level tier:
  - **No-Grade (1-19)**: Top NG Weapon, Armor (Wooden / Devotion) & Jewels.
  - **D-Grade (20-39)**: Top D Weapon (Elven Bow, Tarbar, Staff of Life) & Armor (Brigandine, Manticore, Elven Mithril).
  - **C-Grade (40-51)**: Top C Weapon (Eminence Bow, Homunkulus, Berserker Blade) & Armor (Full Plate, Plated Leather, Carmian).
  - **B-Grade (52-60)**: Top B Weapon (Bow of Peril, Great Sword, Valhalla) & Armor (Zubei, Avadon, Blue Wolf).
  - **A-Grade (61-75)**: Top A Weapon (Soul Bow, Dark Legion, Dasparion's Staff) & Armor (Tallum, Dark Crystal, Majestic, Nightmare).
  - **S-Grade (76-85)**: Top S Weapon (Draconic Bow, Angel Slayer, Arcana Mace) & Armor (Imperial Crusader, Draconic, Major Arcana).

### 4. Infinite Health Potions
- Infinite Health Potion consumption for Hunters & Companions when HP < 80%.

### 5. Companion Skill Catalogue by Grade & Party-Wide Healing/Buffing
- Companion Healer operates as a Party Member and applies Heals and Buffs to **all party members** (Master + Party):
  - **No-Grade (1-19)**: `Heal`, `Cure Poison`, `Might` L1, `Shield` L1.
  - **D-Grade (20-39)**: `Battle Heal`, `Group Heal`, `Might` L2, `Shield` L2, `Wind Walk` L1, `Acumen` L1.
  - **C-Grade (40-51)**: `Major Heal`, `Group Heal` L2, `Purify`, `Might` L3, `Shield` L3, `Wind Walk` L2, `Acumen` L2, `Haste` L1, `Empower` L1.
  - **B-Grade (52-60)**: `Major Heal` L2, `Greater Battle Heal`, `Acumen` L3, `Haste` L2, `Empower` L3, `Focus` L2, `Death Whisper` L2.
  - **A-Grade (61-75)**: `Major Heal` Top, `Restore Life`, `Cleanse`, `Mass Resurrection`, `Focus` L3, `Death Whisper` L3, `Berserker Spirit` L2.
  - **S-Grade (76-85)**: `Salvation`, `Major Heal` Max, `Sublime Self-Sacrifice` (emergency), `Cleanse`, `Cov` / `PoW` / `Magnus` Chant/Prophecy + Max Base Buffs.

### 6. Faithful Field AI (PvE & PvP Simulation)
- **Target Selection**: Smart aggro management in PvE; target healers/magers first in PvP retaliation.
- **Kiting & Positioning**: Ranged bots (Archers, Mages) maintain distance and kite when melee targets approach. Motion vectors include natural human micro-variations.
- **PvP Anti-Gank Retaliation**: If attacked by real players, bot retaliates or uses safety flee / Scroll of Escape when HP < 30%.
- **Looting & Rest**: 1-3 second loot pause after mob kills (`pickUpItem`); sit down to recover MP when MP < 10%.

### 7. Interactive Companion Management UI (`.companion` / Alt+B)
- HTML Control Panel allowing players to:
  - 🔄 **Recall / Teleport Companion to Me**
  - 🛡️ **Toggle Auto-Buffs**
  - ⚔️ **Select Combat Stance** (`Passive/Heal Only`, `Aggressive Support`, `Buff Only`)
  - ⬆️ **Level Sync & Re-Equip**: Sync companion level to master & equip top grade gear.
  - ❌ **Dismiss Companion**

### 8. Dashboard Live Chat Location Tracking & Region Filtering
- Record speaker coordinates (`x`, `y`, `z`) and `regionName` in `WebAPIManager.ChatMessageRecord`.
- Display location tag in Live Chat Terminal: `[ALL] PlayerName (@Gludio Town [-14200, 123100, -3100]): "Message"`.
- Add **Region Filter Dropdown** in the GM Dashboard Live Chat section to filter messages by map region.

### 9. Complete Dashboard GM Control Panel for FakePlayers
- **Location Inspection & Teleport Override**: View X, Y, Z + Zone name for every bot; GM can move/teleport bot to any town/zone, to GM's current position, or custom coordinates.
- **Bot Activation & Reload**: Toggle individual bots ON/OFF (Spawn / Despawn / Sleep) and trigger instant XML/profile reload.
- **Behavior Stance Control**: Dynamically modify bot AI behavior (`HUNTING`, `SAFETY_FLEE`, `PAUSE_IDLE`, `PVP_AGGRESSIVE`, `SELLING`, `FARM_SOLO`).
- **Level & Equipment Grade Control**: Modify bot level (1-85) and force re-equip to selected Grade Tier (No-Grade, D, C, B, A, S) directly from the Web Dashboard.

## Impacted Components
- `com.l2journey.gameserver.model.actor.instance.FakePlayer`
- `com.l2journey.gameserver.model.actor.instance.MercenaryInstance`
- `com.l2journey.gameserver.managers.MercenaryManager`
- `com.l2journey.gameserver.managers.FakeHunterManager`
- `com.l2journey.gameserver.managers.FakeTraderManager`
- `com.l2journey.gameserver.managers.WebAPIManager`
- `com.l2journey.gameserver.network.clientpackets.Say2`
- `com.l2journey.gameserver.handler.voicedcommandhandlers.CompanionVoicedCommandHandler`
- `com.l2journey.gameserver.data.xml.impl.FakePlayerEquipmentData`
- `web/index.html`

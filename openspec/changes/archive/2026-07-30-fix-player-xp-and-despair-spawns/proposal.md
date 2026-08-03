# Change Proposal: Fix Player XP, Mercenary Companion Party UI & Despair Spawns

## Executive Summary
This proposal addresses player EXP gain, Mercenary Companion party interface integration, tester character level/gear setup, and permanent active FakePlayer composition in *Ruins of Despair*.

## Core Goals
1. **Fix Player EXP Gain & Level Gap Penalty**:
   - Resolve 0% EXP distribution issue caused by party level gaps (>20 levels) when high-level mercenaries/companions are hired.
   - Match Mercenary Companion level dynamically to player master level (Level 20 for tester).
   - Exempt Mercenary Companion from party level gap cutoff and EXP dilution so the player receives 100% of hunting XP.
2. **Mercenary Companion Party UI & Auto-Teleport Fix**:
   - Transmit `PartySmallWindowAll` server packet when creating programmatic parties so the Mercenary appears cleanly on the Lineage II client Party UI (`ALT+F`).
   - Ensure instant auto-teleport for Mercenary Companions upon player teleports.
3. **Tester Character Setup**:
   - Set tester character to Level 20 with Top D-Grade equipment (Set, Weapon, Jewels).
4. **5 Permanent Active Fake Players in Ruins of Despair (No Sleep Mode)**:
   - Fix XML spawn coordinates for *Ruins of Despair* to `X: -19120, Y: 136816, Z: -3752` (outside town limits).
   - Maintain 5 permanently active bots regardless of human player presence:
     - 1 Solo Archer (Kiting + Soulshots)
     - 3-Bot Party: 1 Tank (Hate/Taunt) + 1 Healer (Group Heal/Buffs) + 1 Dagger (Backstab/Blows)
     - 1 Dwarf Spoil (Spoil skill + Sweep corpse looting)

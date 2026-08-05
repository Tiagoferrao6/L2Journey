# Coop Companion Spec - Delta Spec for AI Personas & Tactical Profiles

## ADDED Requirements

### Requirement: AI Persona Profiles Architecture

The system SHALL support decoupled AI Persona Profiles (Crystal, Esquizitinha, Shirou) defining tactical behavior, spatial positioning, KS/PK reactions, and class skill execution.

#### Scenario: Crystal Archer Kiting and KS Reaction
- **GIVEN** an AI companion with the Crystal persona profile
- **WHEN** attacking a target at max range or encountering a KS attempt
- **THEN** it SHALL execute kiting movement when targets approach within 300 range and cast Stunning Shot on players attempting KS.

#### Scenario: Esquizitinha Healer Limiter Management and Clutch Protection
- **GIVEN** an AI companion with the Esquizitinha persona profile in a party with a Destroyer or Tyrant
- **WHEN** the Destroyer HP drops to 30% for Frenzy activation or an ally HP drops to 10%
- **THEN** it SHALL suppress Balance Life during Frenzy setup, cast Major Heal immediately post-buff, and cast Celestial Shield on 10% HP clutch targets.

#### Scenario: Shirou Melee Train Aggro and AoE Crowd Control
- **GIVEN** an AI companion with the Shirou persona profile on a Warlord or Paladin
- **WHEN** farming in mob dense areas or receiving a KS attempt
- **THEN** it SHALL pull 5 to 10 mobs before executing Howl and AoE damage skills, and cast Shock Stomp AoE stun upon KS attempts.

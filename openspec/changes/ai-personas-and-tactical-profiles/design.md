# Design: AI Personas & Tactical Combat Profiles

## Technical Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       AIPersonaProfileManager                               │
├─────────────────────────────────────────────────────────────────────────────┤
│  Estrutura de Perfis JSON/Java (Crystal, Esquizitinha, Shirou)              │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                 TACTICAL COMBAT EVALUATOR & DECISION ENGINE                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  • Crystal Engine:                                                          │
│    - Max Range Positioning & Kite Intent                                    │
│    - KS Reaction: Stunning Shot on Player Target                            │
│    - PvP Escape: Entangle + Hit&Run + Escape Scroll if HP < 30%            │
│                                                                             │
│  • Esquizitinha Engine:                                                     │
│    - Limiter Lock: Disable Balance Life during Frenzy/Zealot HP trigger     │
│    - Aggro Control: Trance (Sleep) on add & LoS Cover behind terrain        │
│    - Priority 1: Cleanse on Stun/Silence/Para & Celestial Shield at 10% HP  │
│                                                                             │
│  • Shirou Engine:                                                           │
│    - Warlord Mode: Train 5-10 mobs + Howl + AoE Skills                      │
│    - Paladin Mode: Aggression + Angelic Icon + Sacrifice                    │
│    - KS Reaction: Shock Stomp AoE Stun                                      │
│    - PvP Defence: Rush + Ultimate Defense/Vengeance at 30% HP               │
│                                                                             │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       OOG PROTOCOL DISPATCHER & GAME CORE                   │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Key Decisions

1. **Decoupled Persona Engine**:
   - Os perfis são carregados via `AIPersonaProfileManager` como instâncias de `AIPersonaProfile`. Qualquer personagem OOG pode ter um perfil atribuído.

2. **Spatial Combat Logic (Kiting & LoS)**:
   - *Crystal*: Calcula distância `bot.calculateDistance3D(target)`. Se `< 300`, executa vetor de afastamento por 2s antes do novo ataque.
   - *Esquizitinha*: Verifica linha de visão (`GeoEngine.canSeeTarget`) e busca pontos de cobertura no terreno durante recarga de habilidades.

3. **Limiter & Clutch Protection (Esquizitinha)**:
   - Impede o uso de *Balance Life* quando aliados com classe *Destroyer/Titan/Tyrant* estão com HP entre 15% e 32%.
   - Monitora picos de dano por segundo (DPS recebido). Se HP do alvo cair para `< 10%`, conjura *Celestial Shield* imediatamente.

4. **Crowd Control & Aggro Train (Shirou)**:
   - Puxa até 10 alvos em raio de 600 unidades antes de parar e disparar *Howl* e habilidades de dano em área (*Thunder Storm*, *Earthquake*).

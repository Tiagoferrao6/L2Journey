# Design: Game Master Autônomo / Storyteller (Eventos Dinâmicos)

## Architecture

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                      World Activity & Metrics Monitor                       │
 │      (Monitors: Player Count, Active Zones, Server Time, Player Levels)    │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                    Autonomous GM Director (Gemini API)                      │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • Decides when to trigger an event (e.g. "Orchard Raid in Dion")          │
 │  • Crafts lore text, event duration, and reward conditions                  │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         Game Master Execution Engine                        │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • Spawns event monsters & event boss via Admin Commands API                │
 │  • Broadcasts global announcements to all online players                      │
 │  • Grants rewards (Adena / Event Tokens) to winning players                 │
 └─────────────────────────────────────────────────────────────────────────────┘
```

## Event Types
1. **World Raids / Invasões**: Invasão surpresa de mobs temáticos em cidades ou saídas de vilas.
2. **Bounty Hunt / Caça à Recompensa**: Um monstro mutante spawnado em local secreto com anúncio de charada.
3. **Hero Duel Challenge**: Bot campeão desafia oponentes na arena com anúncio no chat global.

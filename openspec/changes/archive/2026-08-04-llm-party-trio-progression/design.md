# Design: Trio Companheiro Co-op (Tanker, Arqueiro, Bishop)

## Architecture Diagram

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         JOGADOR HUMANO (LÍDER DA PT)                        │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │ (Comandos de Chat / Whisper / Party)
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                       LLM COMPANION MANAGER (TRIO)                          │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │                                                                             │
 │   ┌───────────────────┐    ┌───────────────────┐    ┌───────────────────┐   │
 │   │    PaladinBot     │    │    HawkeyeBot     │    │     BishopBot     │   │
 │   │   (Class: Knight) │    │   (Class: Rogue)  │    │   (Class: Cleric) │   │
 │   │   Role: Tanker    │    │   Role: Ranged DPS│    │   Role: Healer    │   │
 │   └─────────┬─────────┘    └─────────┬─────────┘    └─────────┬─────────┘   │
 └─────────────┼────────────────────────┼────────────────────────┼─────────────┘
               │                        │                        │
               └────────────────────────┼────────────────────────┘
                                        │ (HTTP Async REST Requests)
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                   OLLAMA SERVICE (MODEL: qwen2.5:1.5b)                      │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • Prompt PaladinBot  -> Persona Tanker Destemido                           │
 │  • Prompt HawkeyeBot  -> Persona Arqueiro Focado                            │
 │  • Prompt BishopBot   -> Persona Curador Cuidadoso                          │
 └─────────────────────────────────────────────────────────────────────────────┘
```

## Companions Data Matrix

| Nome | Classe Base (Nv 1) | 1ª Classe (Nv 20) | 2ª Classe (Nv 40) | Papel Principal | Equipamentos Iniciais |
|---|---|---|---|---|---|
| **PaladinBot** | Human Fighter | Knight | Paladin | Tanker / Provocar | Wooden Armor, Short Sword, Shield |
| **HawkeyeBot** | Human Fighter | Rogue | Hawkeye | Ranged DPS Assist | Leather Armor, Bow |
| **BishopBot** | Human Mystic | Cleric | Bishop | Healer / Support | Devotion Set, Apprentice Staff |

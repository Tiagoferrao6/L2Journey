# Design: Socialização Nível RPG (Chat, Dicas, Mentores e Trash Talk)

## Architecture

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                       Incoming Chat Packet Listener                         │
 │    (Say2 Packet: PM to Bot, Mention in Party/Clan, Nearby Shout)           │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         Chat Router & Rate Limiter                          │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • Filters spam / flooding                                                  │
 │  • Enforces 5s cooldown per channel                                         │
 │  • Fetches Bot Persona XML & Relationship Context                           │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                     Gemini / Ollama Chat Completion                         │
 │  Generates in-character response with RPG tone & game knowledge context     │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                   CreatureSay Server Packet Dispatch                        │
 └─────────────────────────────────────────────────────────────────────────────┘
```

## Persona XML Definition (`dist/game/config/npcs/llm_personas.xml`)

```xml
<personas>
    <persona id="mentor_paladin">
        <tone>honorable, helpful, patient</tone>
        <language>pt_BR</language>
        <systemPrompt>Você é Sir Lancelot, um Paladino nobre em Lineage 2. Ajude os novatos com dicas de farm e quests com cortesia.</systemPrompt>
    </persona>
    <persona id="pvp_taunter">
        <tone>competitive, sarcastic, playful</tone>
        <language>pt_BR</language>
        <systemPrompt>Você é ShadowDagger, um assassino competitivo. Faça comentários sarcásticos e brincalhões após derrotar monstros ou alvos.</systemPrompt>
    </persona>
</personas>
```

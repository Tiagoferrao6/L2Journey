# Design: Painel Inspetor Detalhado de Fake Hunters no Dashboard de GM

## Architecture & REST Contract

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         GM Web Dashboard Frontend                           │
 │  (User clicks a FakePlayer row in the Admin table ➔ Fetch /api/admin/...)   │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │ (HTTP GET with Bearer Token)
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         WebAPIManager (Embedded HTTP)                       │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • AdminFakePlayerDetailHandler (/api/admin/fakeplayers/{name})             │
 │  • Queries FakeHunterManager / FakeTraderManager for target FakePlayer      │
 │  • Serializes HP/MP/CP, Inventory items, Paperdoll, Buffs, Skills           │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                    FakePlayer Model (Memory Data Store)                     │
 └─────────────────────────────────────────────────────────────────────────────┘
```

## JSON Payload Structure (`GET /api/admin/fakeplayers/{name}`)

```json
{
  "name": "DespairArcher",
  "level": 40,
  "className": "Silver Ranger",
  "hp": 2450, "maxHp": 2450,
  "mp": 890, "maxMp": 1100,
  "cp": 1200, "maxCp": 1200,
  "location": { "x": -19120, "y": 136816, "z": -3752, "town": "Gludio Town" },
  "equipment": {
    "weapon": "Eminence Bow",
    "chest": "Plated Leather Armor",
    "legs": "Plated Leather Gaiters"
  },
  "inventory": [
    { "itemId": 1463, "name": "Soulshot: C-grade", "count": 1500 },
    { "itemId": 1060, "name": "Lesser Healing Potion", "count": 25 }
  ],
  "buffs": [
    { "skillId": 1068, "name": "Might", "level": 3, "durationSec": 1080 },
    { "skillId": 1086, "name": "Haste", "level": 2, "durationSec": 1080 }
  ]
}
```

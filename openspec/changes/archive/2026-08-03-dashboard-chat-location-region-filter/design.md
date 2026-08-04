# Design: Dashboard Web Chat Location Tracking & Region Filter

## Architecture

```
   ┌────────────────────────────────────────────────────────┐
   │             Backend (WebAPIManager.java)              │
   │  REST API: GET /api/admin/chat                         │
   │  Returns JSON:                                         │
   │  {                                                     │
   │    "messages": [                                       │
   │      {                                                 │
   │        "timestamp": 1722686400,                        │
   │        "type": "ALL",                                  │
   │        "sender": "Neko",                               │
   │        "text": "Buying Soulshots",                     │
   │        "x": -14200, "y": 123100, "z": -3100,           │
   │        "regionName": "Gludio Town"                     │
   │      }                                                 │
   │    ]                                                   │
   │  }                                                     │
   └───────────────────────────┬────────────────────────────┘
                               │
                               ▼
   ┌────────────────────────────────────────────────────────┐
   │          Frontend (dist/game/web/index.html)          │
   │                                                        │
   │  1. Header Control:                                    │
   │     [ Select Region: ALL ▼ ]  [ Button: Refresh Chat ] │
   │                                                        │
   │  2. Rendered Line:                                     │
   │     [09:55:10] [ALL] Neko (@Gludio Town                │
   │     [-14200, 123100, -3100]): Buying Soulshots        │
   └────────────────────────────────────────────────────────┘
```

## Technical Details

1. **HTML Modification (`dist/game/web/index.html`)**:
   - In `<div class="card">` for Chat Stream, insert `<select id="chatRegionFilter" class="btn btn-cyber" style="padding: 0.3rem 0.6rem; font-size: 0.75rem; margin-right: 0.5rem;" onchange="applyChatFilter()">` containing default `<option value="ALL">Todas as Regiões</option>`.

2. **JavaScript Formatting & State (`dist/game/web/index.html`)**:
   - Maintain a local array `allChatMessages` fetched from `/api/admin/chat`.
   - Update region `<select>` options dynamically with all distinct `regionName` strings.
   - Render format: `[${time}] [${m.type}] <span class="chat-sender">${m.sender}${loc}</span>: ${m.text}`
     where `loc` is ` (@${m.regionName} [${m.x}, ${m.y}, ${m.z}])` if `m.regionName` exists.
   - Filtering logic compares selected region option against `m.regionName`.

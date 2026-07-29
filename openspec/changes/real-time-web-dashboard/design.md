## Context

O L2Journey necessita de uma plataforma web local que sirva tanto para acompanhamento público dos jogadores quanto para administração do servidor pelo Game Master (GM). As alterações de configuração (Rates, Fake Players, Alt+B) devem ser aplicadas dinamicamente na memória RAM sem reiniciar o processo do GameServer.

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           GameServer (Java Core)                                │
│                                                                                 │
│  ┌───────────────────────┐  ┌──────────────────────┐  ┌──────────────────────┐  │
│  │   Config / Rates      │  │  FakePlayerManager   │  │   CommunityBoard     │  │
│  │ (EXP, SP, Adena, Drop)│  │ (Traders, Hunters)   │  │ (Shop XML / Items)   │  │
│  └───────────▲───────────┘  └──────────▲───────────┘  └──────────▲───────────┘  │
│              │                         │                         │              │
│              └─────────────────────────┼─────────────────────────┘              │
│                                        │ (Hot-Reload / Update Methods)          │
│                                        │                                        │
│                           ┌────────────┴────────────┐                           │
│                           │      WebAPIManager      │                           │
│                           │ (Thread HTTP Isolada)   │                           │
│                           └────────────▲────────────┘                           │
└────────────────────────────────────────┼────────────────────────────────────────┘
                                         │ (Porta 8080 JSON REST API / Token GM)
                                         ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        Dashboard Web & Painel GM (Local)                        │
│                                                                                 │
│  ┌──────────────────────────────────┐    ┌───────────────────────────────────┐  │
│  │     Público (Read-Only)          │    │     Aba Painel GM (Protegida)     │  │
│  │  - Status Online & RAM           │    │  - Alterar Rates (EXP/Adena)      │  │
│  │  - Mercado de Lojas (Gludio)     │    │  - Reload/Toggle FakePlayers      │  │
│  │  - Ranking PvP/PK                │    │  - Reload Alt+B Community Shop    │  │
│  │  - Status Raid Bosses            │    │  - Feed de Chat do Jogo ao Vivo  │  │
│  │                                  │    │  - Monitor de Players Reais       │  │
│  └──────────────────────────────────┘    └───────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## Decisions

- **Autenticação GM com Bearer Token**: A aba de GM envia o header `Authorization: Bearer <WebAdminToken>` em todas as rotas `/api/admin/*`. O token é definido em `server.ini`.
- **Live Hot-Reload de Configurações**:
  - `Config.RATE_XP`, `Config.RATE_SP`, `Config.RATE_DROP_ADENA` atualizados atomicamente na classe `Config`.
  - Métodos `reload()` expostos no `FakeTraderManager`, `FakeHunterManager` e `CommunityBoardHandler`.
- **Live Chat Stream**: O `WebAPIManager` registra um listener estático nos canais de chat do GameServer mantendo uma fila circular (buffer) das últimas 100 mensagens para servir via Polling/SSE.
- **Gerenciamento de Players Reais**: Endpoint `/api/admin/players` permitindo ações administrativas básicas (Kick, Message, Teleport).

## Risks / Trade-offs

- **Risco: Alteração acidental de rates muito altos.**
  - *Mitigação:* O backend valida os valores recebidos via API para que fiquem dentro de limites razoáveis (ex: EXP rate entre 0.1x e 1000x).

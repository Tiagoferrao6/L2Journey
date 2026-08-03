# Technical Design: FakePlayer GM Monitoring Tab

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      GM Control Panel (web/index.html)                      │
│                Aba 🤖 Monitoramento de Fake Players em Tempo Real           │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
            ┌──────────────────────────┴──────────────────────────┐
            ▼                                                     ▼
┌───────────────────────┐                             ┌───────────────────────┐
│ GET /api/admin/fp/list│                             │POST /api/admin/fp/act │
│ (Obter Lista & Status)│                             │ (Executar Ações GM)   │
└───────────────────────┘                             └───────────────────────┘
            │                                                     │
            └──────────────────────────┬────────────────────────## REST API Endpoints & Payloads

### 1. `GET /api/admin/fakeplayers/list`
Retorna a lista completa de todos os Fake Hunters e Fake Traders online:
```json
{
  "totalHunters": 35,
  "totalTraders": 15,
  "players": [
    {
      "name": "HunterOne",
      "type": "HUNTER",
      "level": 76,
      "className": "Sagittarius",
      "x": -14780,
      "y": 125480,
      "z": -3120,
      "zoneName": "Gludio Territory",
      "hpPercent": 88,
      "mpPercent": 95,
      "state": "HUNTING",
      "targetName": "Monster Eye",
      "archetype": "Archer"
    }
  ]
}
```

### 2. `POST /api/admin/fakeplayers/action`
Executa comandos de GM sobre um bot selecionado:
```json
{
  "action": "TELEPORT_GM" | "DESPAWN" | "FORCE_RETREAT",
  "botName": "HunterOne",
  "gmCharName": "AdminGM"
}
```

## Interface do Usuário (Frontend `web/index.html`)

1. **Navegação**: Nova aba `🤖 FakePlayers` visível no Painel de GM após autenticação por token.
2. **Cards Superiores**:
   - Total Hunters Ativos
   - Total Traders (Lojas)
   - Bots em Fuga de Segurança (Safety Flee)
   - Média de HP da Tropa
3. **Tabela Principal**:
   - Filtros por Tipo (Todos, Hunters, Traders) e por Zona.
   - Colunas: Nome, Tipo, Nível/Classe, Coordenadas (X, Y, Z + Zona), Estado Visual (Tag colorida), Barra de Vida/Mana, Alvo Atual, Ações do GM (Botões Teleport GM / Safe Flee / Kick).

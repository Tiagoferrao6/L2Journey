# Design: OOG Protocol Driver & Real Account Dual Control

## Technical Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    LLM COGNITIVE AGENT (MOTOR DE IA)                        │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                   OOG CLIENT SESSION & PROTOCOL DRIVER                      │
├─────────────────────────────────────────────────────────────────────────────┤
│ • OOGClientSession: Gerencia estado da conexão de rede OOG                  │
│ • OOGPacketDispatcher: Converte intenções da LLM em pacotes de rede         │
│ • OOGCharacterCreator: Executa RequestCharacterCreate autonomamente         │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    L2JOURNEY GAMESERVER (CORE ENGINE)                       │
├─────────────────────────────────────────────────────────────────────────────┤
│ • Valida pacotes (Distância, Reúso, Pesos e Condições de Jogo)             │
│ • Handover Handler: Desconecta OOG se o cliente L2.exe do humano logar      │
│ • MySQL Auto-Save: Gravação nativa nas tabelas characters, items, quests    │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Key Decisions

1. **Protocol-Driven Autonomy vs Direct DB Mutations**:
   - A IA nunca emite instruções SQL `UPDATE` ou `INSERT`. Todas as ações trafegam pelos pacotes ou intenções da AI Controller do jogo, garantindo que o servidor aplique todas as regras de validação, limites de tempo e distância.

2. **Handover Concurrency Strategy**:
   - Quando um `GameClient` de um socket humano tenta logar na conta ativa de um bot OOG, o `GameServer` envia o evento `OnConcurrentHumanLogin`, finaliza graciosamente a sessão OOG e cede o controle ao jogador.
   - Quando a conexão humana é encerrada, o evento `OnHumanLogout` notifica a IA para reabrir a sessão OOG e prosseguir com o planejamento cognitivo.

3. **Character Creation Workflow**:
   - A IA consulta a contagem de personagens da conta via `RequestCharSelectData`.
   - Se 0 personagens existirem, invoca `OOGCharacterCreator.create(...)` enviando o nome desejado, `classId` inicial e atributos visuais válidos para o `PlayerTemplateData`.

4. **Multi-Account Orchestration**:
   - O `OOGCompanionOrchestrator` suporta a ativação em massa de pacotes de contas (1 a 9 bots), gerenciando o convite para a Party e a sincronização de papéis em batalhas de Raid Boss.

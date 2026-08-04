# Design: Correção de Comportamentos Automáticos do Agente Companheiro

## Architecture Diagram

```
  ┌─────────────────────────────────────────┐
  │         DISMISS PARTY PELO JOGADOR       │
  └────────────────────┬────────────────────┘
                       │
                       ▼
  ┌─────────────────────────────────────────┐
  │     COMPANION MANAGER (Tick Check)      │
  ├─────────────────────────────────────────┤
  │  • Detecta saída da party               │
  │  • Transita para AUTONOMOUS_SOLO        │
  │  • NÃO re-adiciona na party             │
  └────────────────────┬────────────────────┘
                       │
                       ▼
  ┌─────────────────────────────────────────┐
  │     SOLICITAÇÃO SOB DEMANDA (PM / INV)   │
  │  • PM "party" ou Convite ➔ ACTIVE_COOP  │
  └─────────────────────────────────────────┘
```

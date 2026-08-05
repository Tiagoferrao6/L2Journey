## 1. Integração no WebAPIManager

- [ ] 1.1 Atualizar `AdminFakePlayersHandler` em `WebAPIManager.java` para incluir o trio de bots do `LLMCompanionManager` na busca por nome e na listagem geral (`/api/admin/fakeplayers`).
- [ ] 1.2 Atualizar o handler de métricas em `WebAPIManager.java` para contabilizar os AI Companions na contagem de bots ativos.
- [ ] 1.3 Adicionar a tag de tipo `"COMPANION"` no payload JSON retornado para os bots do Companion Manager.

## 2. Validação e Testes

- [ ] 2.1 Testar requisição `GET /api/admin/fakeplayers` via Web API e verificar a presença de `PaladinBot`, `HawkeyeBot` e `BishopBot`.
- [ ] 2.2 Testar requisição `GET /api/admin/fakeplayers/PaladinBot` e verificar a inspeção de inventário, buffs e localização.

## 1. Trava de Navegação & Micro-Evasão de Geodata

- [ ] 1.1 Adicionar conjunto `_activeNavigatingBots` em `TownWaypointMeshManager.java` para bloquear rotas reentrantes durante navegação urbana ativa.
- [ ] 1.2 Implementar verificação de estagnação ($\Delta X, \Delta Y < 30$) com vetor de micro-evasão lateral no `TownWaypointMeshManager.java`.

## 2. Cooldown de Falha e Compras Proporcionais

- [ ] 2.1 Atualizar `BuyListExecutingEngine.java` com cálculo de compra de pacotes menores de Soulshots baseados na Adena disponível (ex: 50 shots para 500 Adena).
- [ ] 2.2 Adicionar estado de cooldown de compras (`_shopCooldowns`) no `LLMCompanionManager.java` para suprimir `needsConsumableReplenishment` por 60 segundos após falha de Adena.
- [ ] 2.3 Conectar a liberação de caça autônoma (`FARM_ZONE`) quando o bot estiver em cooldown de compras.

## 3. Validação e Testes

- [ ] 3.1 Executar testes unitários em `LLMTownShoppingTest.java` validando travamento de re-entrância e comportamentos de cooldown.
- [ ] 3.2 Simular bot recém-nascido com 200 Adena e verificar se ele transita para caça de mobs sem se prender em loop na cidade.

## 1. Fundação e Refatoração

- [ ] 1.1 Analisar a estrutura atual de `PlayerAI` e `FakeHunterAI`
- [ ] 1.2 Criar enum `FakePlayerRole` (HEALER, TANK, DPS) no modelo do FakePlayer
- [ ] 1.3 Refatorar o método `onEvtThink()` do `FakeHunterAI` para delegar lógica com base no `FakePlayerRole`

## 2. Implementação de Consumíveis Inteligentes

- [ ] 2.1 Adicionar módulo `FakeConsumableEngine` que checa o HP/MP a cada tick
- [ ] 2.2 Implementar lógica para identificar poções de HP/MP no inventário ou simular sua existência
- [ ] 2.3 Atrelar a ativação automática de Soulshots antes de habilidades ofensivas ou de cura

## 3. Mecânicas de Party e Leader Tracking

- [ ] 3.1 Adicionar propriedade `Leader` (`Player` objeto ou ObjectId) no `FakePlayerProfile`
- [ ] 3.2 Implementar rotina de movimentação passiva: se o líder se afastar > 150 range, seguir o líder
- [ ] 3.3 Implementar sistema de Assist: atacar automaticamente o `Leader.getTarget()` se em modo de combate

## 4. IA Avançada de Suporte (FakeHealer)

- [ ] 4.1 Implementar rotina de varredura de HP da Party no `FakeHealerAI`
- [ ] 4.2 Configurar threshold de cura (ex: HP < 50% = Greater Heal)
- [ ] 4.3 Garantir que curar aliados interrompa qualquer ataque em andamento

## 5. Testes

- [ ] 5.1 Invocar um FakeHealer e testar as curas durante combate
- [ ] 5.2 Testar movimentação de Party e Target Assist no Giran Harbor

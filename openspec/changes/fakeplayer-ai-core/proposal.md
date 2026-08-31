## Why

O servidor já possui um sistema base para FakePlayers (`FakeHunterAI` e `FakeTraderAI`), mas a inteligência artificial atual é limitada. Para suportar ecossistemas PvE avançados, interações realistas de bots, e futuramente servir como base para o sistema de "Tag-Team/GTA V", a IA dos FakePlayers precisa ser significativamente aprimorada. Isso permitirá que o servidor seja povoado com personagens autônomos que curam aliados, usam poções, reagem dinamicamente e acompanham um líder.

## What Changes

- Aprimoramento da engine de IA de FakePlayers (`PlayerAI` e `FakeHunterAI`).
- Adição de lógicas de combate dinâmico: uso estratégico de poções, uso de skills de área, e cura em grupo.
- Implementação de um sistema de "Follower/Party" para FakePlayers, permitindo que eles sigam um alvo líder e ajam em sincronia com o grupo.
- Modularização da IA para suportar facilmente diferentes classes e perfis (ex: FakeHealer, FakeTank).

## Capabilities

### New Capabilities
- `fakeplayer-advanced-ai`: Define as capacidades avançadas de combate e suporte (uso de itens, alocação de alvos e cura) para FakePlayers.
- `fakeplayer-party-mechanics`: Define como um FakePlayer interage em grupo, segue um líder e divide assistência.

### Modified Capabilities
- (Nenhuma capacidade existente identificada para modificação de escopo, apenas implementação técnica)

## Impact

- `java/com/l2journey/gameserver/model/actor/fakeplayer/*`: Refatoração nas IAs dos FakePlayers.
- `java/com/l2journey/gameserver/managers/FakePlayerManager.java`: Possíveis ajustes no carregamento e atribuição de IAs.
- Consumo de CPU: Múltiplos bots com IA mais complexa podem aumentar levemente o tick do servidor. Precisaremos garantir que a lógica da IA seja eficiente.

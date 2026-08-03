## Why

Com a infraestrutura do `fake-traders-engine` (Lojas) já construída e a arquitetura `epic-fake-players-dna` (Combate) definida, precisamos de um ambiente para colocar esses sistemas à prova em produção. Focaremos o escopo geográfico em Gludio para testar o ecossistema econômico e de caça, populando a cidade e seus arredores com bots.

## What Changes

- **Gludio Traders**: Configuração no arquivo `fake_traders_spawns.xml` de cerca de 30 Fake Traders (SELL, BUY, CRAFT) espalhados pela praça central e nos corredores de Gludio. Eles usarão configurações de economia de low-grade (NG e D-grade).
- **Gludio Hunters**: Configuração no arquivo `fake_hunters_spawns.xml` de cerca de 30 Fake Hunters que nascerão nos portões de Gludio e irão se direcionar para áreas de caça (ex: Ruins of Agony, Ruins of Despair). 
- **Otimização Regional (Zone Listeners)**: Implementação e calibração fina da detecção de jogadores reais na região de Gludio (Town e Zonas de Caça adjacentes) para colocar os bots de combate locais em estado de suspensão (Sleep) quando não houver ninguém os observando.

## Capabilities

### New Capabilities
- `gludio-trader-configs`: Configurações de mercado específicas para o estágio inicial do jogo.
- `gludio-hunter-configs`: Spawns, classes e DNA configurados para as ruínas e territórios low-level.
- `zone-optimization-module`: A calibração dos listeners de zona para suspender a IA.

### Modified Capabilities

## Impact

- **Database**: Nenhum. Tudo será via XML (Arquitetura Ghost Objects).
- **AI/Controllers**: Usa o `FakeTraderManager` e o `FakeHunterManager` já definidos nas outras proposals.
- **Server Performance**: Com a otimização de zonas ativada em Gludio, o impacto de CPU será restrito aos momentos em que players de verdade visitarem a região.

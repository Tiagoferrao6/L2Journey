## Why

O sistema de Pets e Summons nativo do Lineage 2 é restrito a NPCs pré-configurados e inflexíveis. Queremos expandir essa funcionalidade criando um sistema de "Mercenários", onde os jogadores possam evocar Summons que utilizam templates (skins, skills e stats) baseados em outros jogadores, classes jogáveis ou monstros chefes (ex: um mini-Golkonda). Isso abre portas para monetização, novos estilos de gameplay (jogar solo com um "Pocket Healer") e uma progressão de companions altamente customizável.

## What Changes

- Expansão da engine de `L2Summon` / `L2Pet` para suportar o carregamento dinâmico de `displayId` arbitrários e stats customizados através de itens de invocação.
- Criação de um novo tipo de Item (ex: "Mercenary Contract") que engatilha o spawn de um `L2MercenaryInstance` (nova classe filha de `L2Summon`).
- O `L2MercenaryInstance` usará o painel nativo de controle de Pets do cliente para movimentação, ataque e comandos de parada, preservando a usabilidade nativa do Lineage 2.
- Definição de templates de Mercenários via XML (ex: `mercenaries.xml`), permitindo plugar templates baseados em Elfos, Orcs, ou monstros Boss.

## Capabilities

### New Capabilities
- `mercenary-summon-engine`: Define a estrutura da nova classe `L2MercenaryInstance` e sua integração com a Action Bar de Pets.
- `mercenary-templates`: Define o modelo de dados XML para customização de atributos, skills, e aparências (`displayId` / weapons) dos mercenários.

### Modified Capabilities
- (Nenhuma)

## Impact

- `java/com/l2journey/gameserver/model/actor/instance/`: Adição da nova classe `L2MercenaryInstance`.
- `java/com/l2journey/gameserver/handler/itemhandlers/`: Novo handler para processar os itens de "Contrato de Mercenário".
- Pode impactar o balanço do jogo se os mercenários tiverem stats muito próximos aos de um jogador real. A economia de consumo (ex: Soulshots para pets) precisará ser bem balanceada.

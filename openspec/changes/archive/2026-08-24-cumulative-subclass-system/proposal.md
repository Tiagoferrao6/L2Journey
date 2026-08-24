## Why

Atualmente no L2Journey, ao trocar de subclasse ou ao evoluir personagens, as subclasses operam como perfis isolados. O sistema de **Subclasse Acumulativa (Dual Class)** permite fundir a classe atual do jogador (Main ou Subclasses 1, 2, 3) com uma segunda classe da **mesma raça**, acumulando as habilidades de ambas as classes e ajustando o nível do personagem para 40.

Para permitir total controle e segurança ao administrador do servidor, o sistema incluirá um arquivo de **configuração XML** (`dist/game/config/custom/CumulativeSubclass.xml`). Se o sistema for desativado (`EnableCumulativeSubclass = false`), será adotada a política **HIDE (Reversível)**: o banco de dados preserva o `dual_class_id`, mas a engine de skills omite as habilidades acumuladas e o NPC de fusão é desativado. Ao religar o sistema, todas as habilidades retornam aos personagens automaticamente sem qualquer perda de dados.

## What Changes

- **Arquivo de Configuração XML**: Criação de `dist/game/config/custom/CumulativeSubclass.xml` para habilitar/desabilitar o sistema (`EnableCumulativeSubclass`), controlar a restrição de raça (`SameRaceOnly`), requisitos (nível 75, Golkonda's Horn ID 99000), nível de destino (40) e política de desativação (`DisabledBehavior = HIDE`).
- **Data Handler Java (`CumulativeSubclassData.java`)**: Leitor XML implementando `IXmlReader` para carregar as configurações do servidor.
- **Banco de Dados (SQL)**: Adição da coluna `dual_class_id INT(2) DEFAULT '-1'` nas tabelas `characters` e `character_subclasses`.
- **Core Engine (Skills)**: Atualização do gerenciador de habilidades em `SkillTreeData` para unir (merge) as árvores de habilidades da classe ativa com a `dual_class_id` da mesma raça (quando o sistema estiver `true`), ocultando as habilidades da classe Dual caso o sistema esteja `false` (política HIDE).
- **Modelos Java**: Atualização de `SubClassHolder`, `Player` e consultas SQL associadas para carregar/salvar a Dual Class em cada slot.
- **NPC Manager de Fusão**: Script customizado em Java (`SubclassManager`) para gerenciar o processo de fusão quando o sistema estiver ativo (`true`). Consome o item, atribui o `dual_class_id`, desequipa itens e reduz a EXP/Level para a base do Nível 40.
- **Suporte a FakeHunters**: Integração em `FakePlayerProfile`, `FakePlayerDAO` e `FakeHunterAI` respeitando as flags do XML e a restrição de mesma raça.

## Capabilities

### New Capabilities
- `cumulative-subclasses`: Permite fusão de classes da mesma raça no nível 75+, acúmulo de habilidades e ajuste de nível para 40 via NPC Subclass Manager, com controle completo e reversível via arquivo de configuração XML.

### Modified Capabilities
<!-- Nenhuma capability existente está sendo modificada -->

## Impact

- **Código Afetado**:
  - `com.l2journey.gameserver.data.xml.SkillTreeData`
  - `com.l2journey.gameserver.model.actor.Player`
  - `com.l2journey.gameserver.model.actor.holders.player.SubClassHolder`
  - `com.l2journey.gameserver.model.actor.fakeplayer.FakePlayerProfile`
  - `com.l2journey.gameserver.dao.FakePlayerDAO`
  - `com.l2journey.gameserver.model.actor.fakeplayer.FakeHunterAI`
- **Novos Arquivos**:
  - `dist/game/config/custom/CumulativeSubclass.xml`
  - `java/com/l2journey/gameserver/data/xml/CumulativeSubclassData.java`
  - `dist/game/data/scripts/custom/SubclassManager/SubclassManager.java`
  - Patch SQL para `characters` e `character_subclasses`.
- **APIs & Pacotes do Cliente**: `UserInfo`, `SkillList`, `ItemList`, `AnnounceToAll`.

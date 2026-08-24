## 1. Configuração XML & Data Handler

- [x] 1.1 Criar o arquivo de configuração XML `dist/game/config/custom/CumulativeSubclass.xml` com parâmetros `EnableCumulativeSubclass`, `SameRaceOnly`, `RequiredLevel`, `RequiredItemId`, `RequiredItemCount`, `DelevelTargetLevel` e `DisabledBehavior = HIDE`.
- [x] 1.2 Criar a classe Data Handler `CumulativeSubclassData.java` em `com.l2journey.gameserver.data.xml` implementando `IXmlReader` para carregar as configurações do XML.

## 2. Camada de Banco de Dados (SQL Patch)

- [x] 2.1 Criar script SQL para adicionar a coluna `dual_class_id INT(2) DEFAULT '-1'` nas tabelas `characters` e `character_subclasses`.

## 3. Modelos Java & Persistência (Core)

- [x] 3.1 Adicionar o atributo `_dualClassId` com getters/setters em `SubClassHolder.java`.
- [x] 3.2 Adicionar `_mainDualClassId`, métodos `getDualClassId()` e `setDualClassId(int dualClassId)` com validação de mesma raça em `Player.java`.
- [x] 3.3 Atualizar as queries SQL de restore/insert/update (`RESTORE_CHARACTER`, `UPDATE_CHARACTER`, `RESTORE_CHAR_SUBCLASSES`, `ADD_CHAR_SUBCLASS`, `UPDATE_CHAR_SUBCLASS`) em `Player.java` para ler e salvar o `dual_class_id`.

## 4. Engine de Skills & Política HIDE (SkillTreeData)

- [x] 4.1 Atualizar `getAvailableSkills` em `SkillTreeData.java` para verificar `Config.ENABLE_CUMULATIVE_SUBCLASS`. Se `true` e `dual_class_id != -1`, mesclar a árvore de skills da classe ativa com a classe Dual.
- [x] 4.2 Garantir que, se `ENABLE_CUMULATIVE_SUBCLASS` for `false` (política HIDE), a engine omita a fusão e retorne apenas as skills da classe ativa, mantendo o `dual_class_id` no DB.

## 5. Camada de Script (NPC Subclass Manager)

- [x] 5.1 Criar a estrutura e arquivo Java `SubclassManager.java` em `dist/game/data/scripts/custom/SubclassManager/`.
- [x] 5.2 Implementar verificação de flag XML (`ENABLE_CUMULATIVE_SUBCLASS`), exibindo HTML de sistema desativado se `false`.
- [x] 5.3 Implementar validações no NPC: Level >= 75, posse de 1 `Golkonda's Horn` (ID 99000), validação estrita de mesma raça (`activeClass.getRace() == selectedDualClass.getRace()`) e bloqueio de autofusão.
- [x] 5.4 Implementar ações do NPC: consumo de item, atribuição de `dual_class_id`, delevel para Nível 40 com EXP base (`getExpForLevel(40)`), un-equip completo de papelaria (itens), atualização de cliente (`UserInfo`, `SkillList`, `ItemList`) e anúncio global (`AnnounceToAll`).

## 6. Suporte para FakeHunters & Verificação

- [x] 6.1 Adicionar suporte a `dual_class_id` em `FakePlayerProfile.java`, `FakePlayerDAO.java` e `FakeHunterAI.java`, respeitando as flags do XML.
- [x] 6.2 Compilar o projeto e realizar verificação completa de todas as camadas e do comportamento ao chavear a flag XML `EnableCumulativeSubclass`.

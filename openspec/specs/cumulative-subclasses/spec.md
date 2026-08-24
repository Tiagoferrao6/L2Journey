## ADDED Requirements

### Requirement: Carregamento de Configuração XML
O sistema MUST carregar as configurações do arquivo `dist/game/config/custom/CumulativeSubclass.xml` através da classe `CumulativeSubclassData` implementando `IXmlReader`.

#### Scenario: Leitura bem-sucedida do arquivo XML
- **WHEN** o servidor é inicializado ou o comando de reload de XML é executado
- **THEN** o sistema lê os parâmetros `EnableCumulativeSubclass`, `SameRaceOnly`, `RequiredLevel`, `RequiredItemId`, `RequiredItemCount`, `DelevelTargetLevel` e `DisabledBehavior`.

### Requirement: Política de Desativação HIDE (Reversível)
O sistema MUST suportar a política de desativação HIDE. Quando `EnableCumulativeSubclass` for `false`, o sistema MUST preservar os registros de `dual_class_id` no banco de dados, porém ocultar as habilidades da Dual Class e desabilitar o NPC de fusão.

#### Scenario: Desativação do sistema via XML (EnableCumulativeSubclass = false)
- **WHEN** a configuração `EnableCumulativeSubclass` for alterada para `false` no XML e o servidor recarregar
- **THEN** o sistema omite as habilidades acumuladas da classe Dual durante a execução de `rewardSkills()`, mantendo o `dual_class_id` intacto no banco de dados.

#### Scenario: Reativação do sistema via XML (EnableCumulativeSubclass = true)
- **WHEN** a configuração `EnableCumulativeSubclass` for alterada para `true` no XML
- **THEN** o sistema restaura automaticamente a fusão das habilidades para todos os personagens que já possuíam `dual_class_id` gravado no banco de dados.

### Requirement: Registro e Armazenamento de Dual Class no Banco de Dados
O sistema MUST armazenar e restaurar a coluna `dual_class_id` para a classe principal na tabela `characters` e para cada subclasse na tabela `character_subclasses`.

#### Scenario: Restaurar personagem com Dual Class configurada
- **WHEN** o personagem faz login no jogo ou alterna para uma classe que possui `dual_class_id` diferente de -1
- **THEN** o sistema carrega o `dual_class_id` correspondente ao slot ativo e disponibiliza a propriedade no objeto `Player`.

### Requirement: Restrição de Mesma Raça para Fusão de Dual Class
O sistema MUST validar que a classe selecionada para fusão como Dual Class pertença estritamente à mesma raça (`PlayerClass.getRace()`) da classe ativa do jogador, quando `SameRaceOnly` for `true`.

#### Scenario: Tentativa de fusão com classe de raça diferente
- **WHEN** o jogador tenta fundir sua classe ativa com uma classe de raça diferente (ex: Human tentando fundir com Dark Elf)
- **THEN** o sistema recusa a ação, exibe uma mensagem explicativa e cancela o processo sem consumir itens ou alterar o nível.

#### Scenario: Tentativa de fusão com classe da mesma raça
- **WHEN** o jogador nível >= 75 com 1 Golkonda's Horn tenta fundir sua classe ativa com uma classe da mesma raça
- **THEN** o sistema aprova a fusão, consome o item, atribui o `dual_class_id`, desequipa itens, ajusta o nível/EXP para a base do Nível 40 e re-entrega as habilidades.

### Requirement: Fusão e Acúmulo de Árvores de Skills
O sistema MUST fundir as árvores de habilidades da classe ativa com a classe Dual configurada (`dual_class_id`) quando o sistema estiver ativo (`EnableCumulativeSubclass = true`), permitindo o aprendizado e retenção de ambas as listas de habilidades sem duplicatas de ID no mesmo nível.

#### Scenario: Aprendizado automático ou manual de habilidades fundidas
- **WHEN** o personagem nível 40+ ganha níveis ou solicita o aprendizado de skills e o sistema está ativo
- **THEN** o sistema consulta `SkillTreeData.getAvailableSkills` e oferece habilidades elegíveis tanto da classe ativa quanto da Dual Class configurada.

### Requirement: Suporte a Subclasse Acumulativa em FakeHunters
O sistema MUST permitir que perfis de FakeHunters possuam e utilizem o acúmulo de habilidades de Dual Class mantendo a mesma raça da classe base do bot, respeitando as configurações do XML.

#### Scenario: Spawn de FakeHunter com Dual Class
- **WHEN** o gerenciador de FakeHunters inicializa um bot com `dual_class_id` configurado e o sistema está ativo
- **THEN** o bot recebe as habilidades da classe principal e da classe Dual e as utiliza em combate.

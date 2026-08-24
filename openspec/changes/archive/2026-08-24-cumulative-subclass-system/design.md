## Context

O L2Journey possui suporte nativo a subclasses (Main class_index 0, e Subclasses 1, 2, 3 salvas na tabela `character_subclasses`). No modelo original, alternar entre a classe principal e as subclasses resulta na substituição total do conjunto de habilidades (skills).

O **Sistema de Subclasse Acumulativa (Dual Class)** introduz a capacidade de fundir a classe ativa de um slot (Main ou Sub) com uma segunda classe da **mesma raça**, permitindo o aprendizado e acúmulo concomitante de habilidades das duas classes no mesmo slot, retornando o personagem para o Nível 40 no momento da fusão.

Para prover controle dinâmico ao administrador, o sistema conta com um arquivo de configuração XML dedicado (`dist/game/config/custom/CumulativeSubclass.xml`) e implementa a política de desativação **HIDE (Reversível)**.

## Goals / Non-Goals

**Goals:**
- Criar o arquivo de configuração XML (`dist/game/config/custom/CumulativeSubclass.xml`) e seu carregador Java (`CumulativeSubclassData.java`).
- Suportar a política de desativação **HIDE (Reversível)**: ao desligar a flag no XML (`EnableCumulativeSubclass = false`), o `dual_class_id` é mantido no DB, mas a engine de skills omite as habilidades secundárias e o NPC é desabilitado. Ao religar (`true`), todas as habilidades acumuladas voltam automaticamente.
- Adicionar suporte a `dual_class_id` no banco de dados (`characters` e `character_subclasses`).
- Atualizar a engine de habilidades (`SkillTreeData.java`) para fundir (merge) as árvores de habilidades da classe ativa com a `dual_class_id` somente quando o sistema estiver ativado no XML.
- Garantir a restrição estrita de **mesma raça** (`activeClass.getRace() == dualClass.getRace()`).
- Implementar o NPC de fusão (`SubclassManager.java`) respeitando o status do XML.
- Integrar o suporte de Subclasse Acumulativa para **FakeHunters**.

**Non-Goals:**
- Excluir permanentemente dados de Dual Class ao desativar o sistema (o modo padrão é HIDE / Reversível).
- Permitir fusão entre classes de raças diferentes.
- Permitir fusão de classe com ela mesma.

## Decisions

### 1. Arquivo de Configuração XML e Data Handler
- **Decisão**: Criar `dist/game/config/custom/CumulativeSubclass.xml` e `CumulativeSubclassData.java` (implementando `IXmlReader`).
- **Estrutura XML**:
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../xsd/cumulativeSubclass.xsd">
      <setting name="EnableCumulativeSubclass" val="true" />
      <setting name="SameRaceOnly" val="true" />
      <setting name="RequiredLevel" val="75" />
      <setting name="RequiredItemId" val="99000" />
      <setting name="RequiredItemCount" val="1" />
      <setting name="DelevelTargetLevel" val="40" />
      <setting name="DisabledBehavior" val="HIDE" />
  </list>
  ```
- **Razão**: Permitir que administradores configurem e alterem o comportamento do sistema dinamicamente sem recompilação.

### 2. Política de Desativação HIDE (Reversível)
- **Decisão**: Quando `EnableCumulativeSubclass == false`:
  - O banco de dados preserva o `dual_class_id` armazenado nos personagens.
  - Em `SkillTreeData.getAvailableSkills()`, a verificação de `dual_class_id` é ignorada se o sistema estiver desativado no XML, retornando apenas as habilidades da classe principal.
  - O NPC `SubclassManager` exibe mensagem de sistema desativado.
  - Ao reativar a flag para `true`, na próxima atualização de habilidades (`rewardSkills()`) ou login, todas as habilidades da Dual Class voltam a ser concedidas aos personagens.
- **Alternativas consideradas**:
  - *Limpeza Definitiva (PURGE)*: Deletaria os dados no DB, impedindo a recuperação caso o admin quisesse testar ou desligar temporariamente.
- **Razão**: Garante total segurança aos dados dos jogadores e flexibilidade para o servidor.

### 3. Suporte a Dual Class por Slot (Main e Subclasses 1..3)
- **Decisão**: Adicionar `dual_class_id` na tabela `characters` (para class_index 0) e na tabela `character_subclasses` (para class_index 1..3).
- **Razão**: Otimiza a performance de leitura mantendo coerência com o modelo `SubClassHolder` e `Player`.

### 4. Algoritmo de Fusão de Skills em `SkillTreeData.java`
- **Decisão**: Unir os mapas de `SkillLearn` da classe principal e da classe Dual utilizando a hash da skill (`SkillData.getSkillHashCode(skillId, level)`). Em caso de colisões no mesmo nível da habilidade, a entrada com menor `getLevel()` é mantida.

### 5. Trava Estrita de Raça (Same-Race Policy)
- **Decisão**: Validar `activeClass.getRace() == dualClass.getRace()` tanto no script do NPC `SubclassManager` quanto no método `setDualClassId(int id)` em `Player.java`.

## Risks / Trade-offs

- **[Risco] Jogadores mantendo delevel 40 caso o sistema seja desligado (HIDE)**  
  *Mitigação*: O nível e EXP permanecem no nível 40 onde o jogador parou, mas apenas as skills adicionais da Dual Class são temporariamente ocultadas enquanto a flag estiver `false`. Ao religar, as skills reaparecem imediatamente.
- **[Risco] Inconsistência ao alterar parâmetros do XML em tempo de execução**  
  *Mitigação*: O `CumulativeSubclassData` oferece suporte a reload via comando de Admin, aplicando imediatamente as novas regras no servidor.

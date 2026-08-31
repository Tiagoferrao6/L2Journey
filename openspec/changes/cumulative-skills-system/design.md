## Context

O L2Journey já possui um suporte estrutural (em Java e no Banco de Dados) para a coluna `dual_class_id` na tabela `character_subclasses`. No entanto, não há um sistema para os jogadores alcançarem essa funcionalidade por si mesmos de forma orgânica (a não ser via GM/SQL). Queremos liberar isso para o público através de uma mecânica de progressão inspirada no Dragon-Network, focada no farm de bosses customizados e restrição de classes da mesma raça.

## Goals / Non-Goals

**Goals:**
- Implementar um item customizado **"Golkonda Horn"** (ID: 99900).
- Criar a Quest `Q999_CumulativePower` para habilitar a Subclasse Acumulativa em troca do Golkonda Horn.
- Criar um NPC ou adaptar o Village Master para que o jogador escolha sua Subclasse Acumulativa obedecendo à restrição de raça.
- Configurar 3 tiers do Raid Boss Golkonda (IDs originais e customizados).

**Non-Goals:**
- Não iremos refatorar o núcleo da engine (`Player.java` e `SkillTreeData`), assumindo que a parte nativa de `dual_class_id` está operante. Focaremos apenas na Quest e no SQL/XML.
- Não permitiremos acumular skills de raças diferentes.

## Decisions

1. **Golkonda Horn (Item Customizado)**
   - ID escolhido: `99900`. 
   - Arquivos alterados: `itemname-e.txt` (via script Python), `dist/game/data/stats/items/custom/horns.xml` (novo).

2. **O Multiverso de Golkonda (NPCs/Bosses)**
   - Tier 1 (Exiled Golkonda): ID customizado `29000`. HP altíssimo, Dano baixo. (The Cemetery).
   - Tier 2 (Original Golkonda): ID `25126`. (Tower of Insolence).
   - Tier 3 (Infernal Golkonda): ID customizado `29001`. HP e Dano colossais. (Monastery of Silence).
   - *Rationale:* Criar NPCs separados no `npc.xml` e no `raidboss_spawnlist.sql` é mais seguro e escalável do que tentar criar instâncias dinâmicas via código para o mesmo ID.

3. **Mecânica de Desbloqueio e Aplicação (NPC)**
   - A Quest/NPC Manager de Subclasse Acumulativa filtrará os IDs das classes disponíveis baseando-se no `player.getRace()`.
   - Quando o jogador escolhe a classe, ela é gravada via SQL `UPDATE character_subclasses SET dual_class_id = ? WHERE charId = ? AND class_index = ?`. O jogador desloga e retorna com a nova classe acoplada.

## Risks / Trade-offs

- **Risk:** Conflito de skills se o jogador tiver a mesma skill na Main e na Dual Class.
  - *Mitigation:* A engine do L2J resolve conflitos pelo maior nível da skill, porém validaremos isso na homologação.
- **Risk:** Os novos Golkondas estarem muito fortes ou muito fracos (balanceamento).
  - *Mitigation:* Configurar seus stats nos XML baseados em multiplicadores de bosses equivalentes do nível 75 e 80.

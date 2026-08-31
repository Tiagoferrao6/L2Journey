## Context
Atualmente, o NPC 39900 (Golkonda's Avatar) utiliza o modelo padrão da NPC Lietta. Queremos substituir o modelo visual pelo próprio Golkonda, mas em um tamanho reduzido (mini-boss) adequado para estar dentro da cidade de Giran.

## Goals / Non-Goals

**Goals:**
- Configurar o NPC 39900 com o modelo visual do boss Golkonda (displayId 25126).
- Manipular a escala de renderização do cliente através do raio e altura de colisão enviados pelo servidor.

**Non-Goals:**
- Não alterar as funções e a lógica do `SubclassManager`.
- Não afetar os stats ou o comportamento de combate do NPC.

## Decisions
- **DisplayId 25126**: Escolhido para usar o modelo visual exato do boss Golkonda, mantendo a coerência com a lore do sistema de Cumulative Skills.
- **Raio 10, Altura 22**: O cliente do Lineage 2 calcula a escala visual de um NPC dividindo a colisão do servidor pela colisão nativa do modelo. O Golkonda original tem raio 42 e altura 94. Configurando 10/22, a escala resulta em aproximadamente ~23%, o tamanho ideal para a cidade.

## Risks / Trade-offs
- O modelo do boss possui armas e efeitos que podem parecer ligeiramente deslocados numa escala muito pequena, porém o "Mini-Golkonda" costuma ser um truque estético comum e bem aceito pela comunidade de Lineage 2.

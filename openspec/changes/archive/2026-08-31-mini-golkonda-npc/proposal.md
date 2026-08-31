## Why
O NPC Manager "Golkonda's Avatar" (NPC 39900) atualmente utiliza o modelo da Lietta (Dwarf Female, id 31267). Para dar um ar épico e focar na temática do sistema de Cumulative Skills (conquistar o chifre do Golkonda), queremos usar o modelo visual do próprio boss Golkonda (id 25126) reduzido em escala.

## What Changes
- Atualização do `displayId` do NPC 39900 para `25126` (modelo do Golkonda) em `custom.xml`.
- Ajuste das propriedades de colisão do NPC 39900 (`radius` reduzido para 10, `height` reduzida para 22) para que o cliente de Lineage 2 o renderize em escala reduzida (aproximadamente 23% do tamanho real do boss).

## Capabilities

### New Capabilities
- `mini-golkonda-npc`: Configuração visual reduzida para o NPC do sistema cumulativo, explorando manipulação de displayId e colisões.

### Modified Capabilities
Nenhum

## Impact
- `dist/game/data/stats/npcs/custom/custom.xml` será alterado. Apenas o aspecto visual e o tamanho do hitbox do NPC 39900 em Giran serão afetados.

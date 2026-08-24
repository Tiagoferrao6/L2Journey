# Proposal: Custom Tattoo System (Conqueror's Badge Economy)

## Summary
Implementar um sistema completo de Tattoos customizadas de Nível 1 ao 6 em 7 arquétipos distintos (Ogre, Monk, Assassin, Blood, Soul, Flame/Witch, Absolute/Divine) equipáveis nos slots de Underwear (Direita) e Hair2 (Esquerda), utilizando unicamente a moeda **Conqueror's Badge** (Item ID `99000`) e ícones visuais nativos das Dyes (Henna).

## Motivation
Atualmente a loja de Tattoos do NPC *Conqueror's Store* possui apenas itens genéricos de defesa física. A criação de um sistema progressivo com 6 níveis e 7 arquétipos oferece objetivos de médio e longo prazo para os jogadores que participam de caçadas a Raid Bosses e Grand Bosses, diversificando builds para classes físicas, mágicas, tanques e atiradores.

## Proposed Changes
1. **Definição de 84 Itens Customizados (`dist/game/data/stats/items/custom/tattoos.xml`)**:
   - 7 Arquétipos x 6 Níveis x 2 Slots (Direito: `underwear`, Esquerdo: `hair2`).
   - Mapeamento sequencial de IDs de `41001` a `41084`.
   - Modificadores XML por porcentagem (`mul order="0x30"`) e valores absolutos (`add order="0x40"`).
   - **Ícones Visuais de Dyes (Henna)**:
     - Níveis 1 e 2: Henna Tier 1 (`icon.etc_*_hena_i00`)
     - Níveis 3 e 4: Henna Tier 2 (`icon.etc_*_hena_i01`)
     - Níveis 5 e 6: Greater Henna Tier 3 (`icon.etc_*_hena_i02`)
   - **Tattoo of Blood (Ajuste de Balanceamento)**:
     - 1º Stat: `absorbDam` (Vampiric Rage: +3% a +20%)
     - 2º Stat: `maxHp` (Max HP: +8% a +20%)
     - 3º Stat (Lv 5 e 6): `cAtk` (Crit Power: +15%/+20%) + `gainHp` (Heal Recv: +15%/+20%)
     - Penalidade P.Def proporcional (Lv 1: -1.5%, Lv 2: -2.5%, Lv 3: -4.0%, Lv 4: -5.5%, Lv 5: -7.5%, Lv 6: -10.0%).

2. **Matriz de Progressão de Atributos**:
   - **Lv 1**: +3% no 1º Atributo
   - **Lv 2**: +5% no 1º Atributo
   - **Lv 3**: +8% no 1º e 2º Atributos
   - **Lv 4**: +11% no 1º e 2º Atributos
   - **Lv 5**: +15% em todos os atributos do arquétipo
   - **Lv 6**: +20% em todos os atributos do arquétipo

3. **Sistema de Compra e Upgrades (`dist/game/data/multisell/900003.xml`)**:
   - **Compra Lv 1 (Direita / Esquerda)**: 20 `Conqueror's Badge` (ID `99000`).
   - **Upgrade Lv 1 ➔ Lv 2**: 25 Badges + Tattoo Lv 1.
   - **Upgrade Lv 2 ➔ Lv 3**: 75 Badges + Tattoo Lv 2.
   - **Upgrade Lv 3 ➔ Lv 4**: 200 Badges + Tattoo Lv 3.
   - **Upgrade Lv 4 ➔ Lv 5**: 500 Badges + Tattoo Lv 4.
   - **Upgrade Lv 5 ➔ Lv 6**: 1.200 Badges + Tattoo Lv 5.

## Impact & Dependencies
- **Moeda**: Exclusivamente `Conqueror's Badge` (`99000`).
- **Core Server**: Sem alterações em código Java; todo o sistema é baseado em definições XML nativas do L2J (Datapack).
- **NPC**: `Conqueror's Store` (NPC ID `90000`) através da multisell `900003.xml`.

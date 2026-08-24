# Design Document: Custom Tattoo System

## System Architecture Overview

```
 ┌─────────────────────────────────────────────────────────────────────────┐
 │                   CONQUEROR'S STORE (NPC 90000)                         │
 └────────────────────────────────────┬────────────────────────────────────┘
                                      │ Opens Multisell 900003.xml
                                      ▼
 ┌─────────────────────────────────────────────────────────────────────────┐
 │                   MULTISELL TATTOO EXCHANGE (900003)                    │
 ├────────────────────────────────────┬────────────────────────────────────┤
 │ BUY LEVEL 1                        │ UPGRADES (LV 1 ➔ LV 6)             │
 │ Custo: 20x Conqueror's Badge       │ Requer: Tattoo Anterior + Badges   │
 └────────────────────────────────────┴────────────────────────────────────┘
```

## 1. Item ID Ranges, Slots & Ícones (Henna Dyes)

Total de **84 itens customizados** divididos em 2 slots de equipamento:

- **Slot Direito (Tatuagem Direita)**: `<set name="bodypart" val="underwear" />`
  - Range de IDs: `41001` a `41042`
- **Slot Esquerdo (Tatuagem Esquerda)**: `<set name="bodypart" val="hair2" />`
  - Range de IDs: `41043` a `41084`

### Mapeamento Detalhado por Arquétipo e Ícones por Nível

A representação visual dos itens utilizará a progressão de ícones nativos das Dyes (Henna) do Lineage 2:
- **Lv 1 & 2**: Ícone Henna Nível 1 (`*_hena_i00`)
- **Lv 3 & 4**: Ícone Henna Nível 2 (`*_hena_i01`)
- **Lv 5 & 6**: Ícone Greater Henna Nível 3 (`*_hena_i02`)

| Arquétipo | Nome Exibido | Slot Direito (Underwear) | Slot Esquerdo (Hair2) | Ícone Base (Henna Dye) |
| :--- | :--- | :--- | :--- | :--- |
| **Ogre** | *Tattoo of Ogre* | `41001` - `41006` | `41043` - `41048` | `icon.etc_str_hena_i00`..`i02` |
| **Monk** | *Tattoo of Monk* | `41007` - `41012` | `41049` - `41054` | `icon.etc_dex_hena_i00`..`i02` |
| **Assassin** | *Tattoo of Assassin* | `41013` - `41018` | `41055` - `41060` | `icon.etc_dex_hena_i00`..`i02` |
| **Blood** | *Tattoo of Blood* | `41019` - `41024` | `41061` - `41066` | `icon.etc_con_hena_i00`..`i02` |
| **Soul** | *Tattoo of Soul* | `41025` - `41030` | `41067` - `41072` | `icon.etc_str_hena_i00`..`i02` |
| **Flame** | *Tattoo of Flame* | `41031` - `41036` | `41073` - `41078` | `icon.etc_int_hena_i00`..`i02` |
| **Absolute** | *Tattoo of Absolute* | `41037` - `41042` | `41079` - `41084` | `icon.etc_men_hena_i00`..`i02` |

---

## 2. Matriz de Atributos e Níveis (1 a 6)

### Regra de Escalonamento de Bônus Geral

| Nível | Bônus Aplicado | Sufixo Ícone |
| :--- | :--- | :--- |
| **Lv 1** | +3% no 1º Atributo | `*_hena_i00` |
| **Lv 2** | +5% no 1º Atributo | `*_hena_i00` |
| **Lv 3** | +8% no 1º e 2º Atributos | `*_hena_i01` |
| **Lv 4** | +11% no 1º e 2º Atributos | `*_hena_i01` |
| **Lv 5** | +15% em todos os Atributos do arquétipo | `*_hena_i02` |
| **Lv 6** | +20% em todos os Atributos do arquétipo | `*_hena_i02` |

### Tabela Detalhada por Arquétipo

1. **Ogre (*O Colosso*)**:
   - 1º Stat: `pAtk` (Ataque Físico)
   - 2º Stat: `maxHp` (HP Máximo)
   - 3º Stat: `pDef` (Defesa Física)

2. **Monk (*O Frenesi*)**:
   - 1º Stat: `pAtkSpd` (Velocidade de Ataque)
   - 2º Stat: `pAtk` (Ataque Físico)
   - 3º Stat: `critRate` (Taxa Crítica)

3. **Assassin (*O Algoz*)**:
   - 1º Stat: `cAtk` (Dano Crítico Físico)
   - 2º Stat: `runSpd` (Velocidade de Movimento)
   - 3º Stat: `rEvas` (Esquiva)

4. **Blood (*O Imortal*)**:
   - **1º Stat:** `absorbDam` (Vampiric Rage):
     - Lv 1: `+3%` | Lv 2: `+5%` | Lv 3: `+8%` | Lv 4: `+11%` | Lv 5: `+15%` | Lv 6: `+20%`
   - **2º Stat:** `maxHp` (Max HP):
     - Lv 1: `0` | Lv 2: `0` | Lv 3: `+8%` | Lv 4: `+11%` | Lv 5: `+15%` | Lv 6: `+20%`
   - **3º Stat (Lv 5 e 6):** `cAtk` (Critical Power: `+15%` no Lv 5, `+20%` no Lv 6) **+** `gainHp` (Heal Recovery: `+15%` no Lv 5, `+20%` no Lv 6)
   - **Penalidade Proporcional de P.Def:** Escala na metade da proporção do bônus com teto estrito de **-10% no Nível 6**:
     - Lv 1: `-1.5% P.Def` (`val="0.985"`)
     - Lv 2: `-2.5% P.Def` (`val="0.975"`)
     - Lv 3: `-4.0% P.Def` (`val="0.960"`)
     - Lv 4: `-5.5% P.Def` (`val="0.945"`)
     - Lv 5: `-7.5% P.Def` (`val="0.925"`)
     - Lv 6: `-10.0% P.Def` (`val="0.900"`)

5. **Soul (*O Atirador*)**:
   - 1º Stat: `accCombat` (Precisão)
   - 2º Stat: `pAtkRange` (Alcance do Arco/Arma)
   - 3º Stat: `critRate` (Taxa Crítica)

6. **Flame / Witch (*O Destruidor Mágico*)**:
   - 1º Stat: `mAtkSpd` (Casting Speed)
   - 2º Stat: `mAtk` (Ataque Mágico)
   - 3º Stat: `mCritRate` (Taxa Crítica Mágica)

7. **Absolute / Divine (*A Muralha*)**:
   - 1º Stat: `pDef` & `mDef` (Defesa Física & Mágica)
   - 2º Stat: `maxCp` (CP Máximo)
   - 3º Stat: `rShld` (Taxa de Bloqueio com Escudo)

---

## 3. Economia de Upgrades (Conqueror's Badge - ID 99000)

| Estágio | Ingrediente 1 | Ingrediente 2 (Moedas) | Recompensa (Produção) |
| :--- | :--- | :--- | :--- |
| **Compra Lv 1** | - | 20 Badges | Tattoo Lv 1 |
| **Upgrade Lv 1 ➔ 2** | Tattoo Lv 1 | 25 Badges | Tattoo Lv 2 |
| **Upgrade Lv 2 ➔ 3** | Tattoo Lv 2 | 75 Badges | Tattoo Lv 3 |
| **Upgrade Lv 3 ➔ 4** | Tattoo Lv 3 | 200 Badges | Tattoo Lv 4 |
| **Upgrade Lv 4 ➔ 5** | Tattoo Lv 4 | 500 Badges | Tattoo Lv 5 |
| **Upgrade Lv 5 ➔ 6** | Tattoo Lv 5 | 1.200 Badges | Tattoo Lv 6 |

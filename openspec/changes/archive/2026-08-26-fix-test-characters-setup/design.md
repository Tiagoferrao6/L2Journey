# Design: Fix Test Characters Setup Architecture

## Context

Esta especificação técnica define a solução para corrigir o provisionamento dos personagens de teste `SilverTester` (`300000000`) e `TitanTester` (`300000001`).

## Architecture & Database Mappings

### 1. Requisitos de Subclasse (`character_subclasses`)

O motor L2J exige que o mapa de subclasses possua obrigatoriamente o `class_index = 0` contendo a classe principal do personagem. Sem essa entrada, a classe ativa falha no carregamento e reverte para a raça/modelo padrão (Female Dwarf, Class ID 0).

```
SilverTester (charId = 300000000, Race = Elf, Sex = Female):
  ├─ class_index = 0: Moonlight Sentinel (Class ID 102) [BASE CLASS]
  ├─ class_index = 1: Duelist (Class ID 88)
  ├─ class_index = 2: DreadNought (Class ID 89)
  └─ class_index = 3: Archmage (Class ID 94)

TitanTester (charId = 300000001, Race = Orc, Sex = Male):
  ├─ class_index = 0: Titan (Class ID 113) [BASE CLASS]
  ├─ class_index = 1: DreadNought (Class ID 89)
  ├─ class_index = 2: Spectral Master (Class ID 111)
  └─ class_index = 3: Ghost Hunter (Class ID 108)
```

### 2. Gestão de Peso (Inventory Weight Limit)

| Item | Quantidade Anterior | Peso Unitário | Peso Total | Status | Quantidade Corrigida | Peso Final |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| **Soulshot S-Grade** | 100.000 | 3 | 300.000 | ❌ Overweight | 5.000 | 15.000 |
| **Blessed Spiritshot S-Grade** | 100.000 | 3 | 300.000 | ❌ Overweight | 5.000 | 15.000 |
| **Armas & Armaduras** | Variado | ~50 | ~90.000 | OK | Variado | ~90.000 |
| **Total do Inventário** | -- | -- | **~690.000** | 🔴 **185.45% Overload** | -- | 🟢 **~120.000 / <100%** |

### 3. Coordenadas de Terreno e Terreno Geodata

- Posição Unificada: `x = 83400`, `y = 147940`, `z = -3404` (Giran Town Square ground mesh).
- Heading: `0` (virado para o centro da praça).

### 4. Tabela de Hero e Clan

```sql
REPLACE INTO `heroes` (`charId`, `class_id`, `count`, `played`, `claimed`, `message`) VALUES
(300000000, 102, 1, 1, 'true', 'SilverTester Hero'),
(300000001, 113, 1, 1, 'true', 'TitanTester Hero');
```

## Why

Atualmente o servidor carece de um sistema de recompensas dinâmico e centralizado para abates de Raid Bosses e Grand Bosses no mundo aberto. A introdução da moeda personalizada **Conqueror's Badge** incentiva o PvP e a caça a chefes de mundo aberto em todas as faixas de nível, permitindo que os jogadores acumulem moedas e troquem por equipamentos de alto valor (Armas, Armaduras, Tattoos) em um NPC dedicado em Giran.

## What Changes

- **Nova Moeda Personalizada (`Conqueror's Badge`)**: Criação do item de moeda no servidor.
- **Sistema de Drop por Faixa de Nível de Boss**: Implementação de recompensa via `dropItem` no chão ao derrotar qualquer Raid Boss ou Grand Boss de mundo aberto:
  - **Lv 20 a 39**: 1 a 3 moedas (`Rnd.get(1, 3)`)
  - **Lv 40 a 51**: 4 a 8 moedas (`Rnd.get(4, 8)`)
  - **Lv 52 a 60**: 10 a 18 moedas (`Rnd.get(10, 18)`)
  - **Lv 61 a 75**: 20 a 35 moedas (`Rnd.get(20, 35)`)
  - **Lv 76 a 85**: 40 a 70 moedas (`Rnd.get(40, 70)`)
- **NPC Comerciante em Giran (`Conqueror's Store`)**:
  - NPC com visual do **Death Lord Shax** (display ID `25282`), porém ajustado para a **altura/colisão de uma Gatekeeper** (escala humana comum).
  - Localizado próximo à Gatekeeper (GK) em Giran.
  - Diálogos HTML categorizados por opções de troca: **Armas**, **Armaduras** e **Tattoos**.
  - Arquivos Multisell prontos para integrar itens padrão e futuros itens personalizados.

## Capabilities

### New Capabilities
- `raid-boss-currency-drop`: Gerenciamento e cálculo do drop automático da moeda `Conqueror's Badge` no chão ao derrotar Raid Bosses / Grand Bosses do mundo aberto por faixa de nível.
- `conqueror-merchant-npc`: NPC de trocas `Conqueror's Store` localizado em Giran (próximo à Gatekeeper), utilizando skin do Death Lord Shax com proporções humanas/GK, contendo HTMLs e listas de trocas Multisell para Armas, Armaduras e Tattoos.

### Modified Capabilities
- Nenhuma funcionalidade pré-existente terá seus requisitos alterados.

## Impact

- **Servidor GameServer / Core**: Adição de manipulador de morte de Raid/GrandBoss ou extensão do `Attackable.java` / Script de Evento para spawn de drops no chão.
- **Data (XML & HTML)**: Novos arquivos de Item, NPC (com modelo do Death Lord Shax redimensionado), HTML e Multisell em `dist/game/data/`.

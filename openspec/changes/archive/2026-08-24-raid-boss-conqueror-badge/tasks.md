# Implementation Tasks

## 1. Item Definition
- [x] Definir novo Item ID para `Conqueror's Badge` em `dist/game/data/stats/items/` com propriedades empilháveis e negociáveis.

## 2. Drop Logic (Raid & Grand Bosses)
- [x] Criar handler/script para escutar morte de RaidBoss e GrandBoss.
- [x] Implementar faixas de nível e sorteio com `Rnd.get(...)`:
  - [x] Lv 20-39: 1-3
  - [x] Lv 40-51: 4-8
  - [x] Lv 52-60: 10-18
  - [x] Lv 61-75: 20-35
  - [x] Lv 76-85: 40-70
- [x] Invocar `dropItem` no chão na posição do boss abatido.

## 3. NPC Merchant & HTMLs
- [x] Registrar NPC Comerciante `Conqueror's Store` em `dist/game/data/stats/npcs/` com mesh/displayId de Death Lord Shax (25282) e altura de GK (`height="30"`).
- [x] Adicionar Spawn do NPC em Giran próximo à GK (`dist/game/data/spawns/`).
- [x] Criar HTML principal do NPC com menu categorizado (Armas, Armaduras, Tattoos).

## 4. Multisell System
- [x] Criar arquivo Multisell para Armas (exigindo `Conqueror's Badge`).
- [x] Criar arquivo Multisell para Armaduras (exigindo `Conqueror's Badge`).
- [x] Criar arquivo Multisell para Tattoos (exigindo `Conqueror's Badge`).

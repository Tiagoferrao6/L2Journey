# Implementation Tasks

## 1. Item Definitions (XML Custom)
- [x] Criar arquivo de itens `dist/game/data/stats/items/custom/tattoos.xml` com os 84 itens customizados (IDs `41001` a `41084`).
- [x] Implementar Slot Direito (`underwear`) para os IDs `41001` a `41042` abrangendo os 7 arquétipos (Níveis 1 ao 6).
- [x] Implementar Slot Esquerdo (`hair2`) para os IDs `41043` a `41084` abrangendo os 7 arquétipos (Níveis 1 ao 6).
- [x] Configurar os modificadores de estatísticas conforme a matriz de escalonamento (Lv 1: +3%, Lv 2: +5%, Lv 3: +8%, Lv 4: +11%, Lv 5: +15%, Lv 6: +20%).

## 2. Multisell Configuration
- [x] Atualizar `dist/game/data/multisell/900003.xml` com as opções de compra inicial de todas as Tattoos Nível 1 (Direita e Esquerda) por 20 `Conqueror's Badge` (ID `99000`).
- [x] Configurar todas as receitas de upgrade (Lv 1➔2: +25 Badges, Lv 2➔3: +75 Badges, Lv 3➔4: +200 Badges, Lv 4➔5: +500 Badges, Lv 5➔6: +1.200 Badges).

## 3. Verification
- [x] Validar estrutura XML contra `items.xsd` e `multisell.xsd`.

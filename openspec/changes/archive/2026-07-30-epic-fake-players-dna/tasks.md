# Tasks: Epic Fake Players DNA (Motor Core de IA e Turnos)

## 1. Parsers XML e Modelos de DNA (Ghost Objects)
- [x] **1.1 Criar o parser e modelo `fake_hunters_dna.xml`**
  - Definição do schema XML para traços comportamentais (0-100): `preservacao`, `sociabilidade`, `ganancia`, `agressividade`, `altruismo`.
- [x] **1.2 Criar o parser e modelo `fake_hunters_spawns.xml`**
  - Definição dos pontos de spawn, rotas, turno de atividade (`MORNING`, `PRIME_TIME`, etc.) e classe/equipamentos do bot.
- [x] **1.3 Carregamento em Memória (`HunterDNA`)**
  - Instanciar e manter os perfis de DNA em RAM vinculados à classe `FakePlayer` (sem persistência SQL).

## 2. Motor Core `FakeHunterManager` & Relógio de Turnos
- [x] **2.1 Implementar a classe `FakeHunterManager`**
  - Criar o singleton com `ThreadPool` exclusiva para atualização de IA de combate dos caçadores.
- [x] **2.2 Relógio Central de Turnos (Shift Engine)**
  - Implementar verificação periódica de turnos (`MORNING`, `AFTERNOON`, `NIGHT`, `PRIME_TIME`).
- [x] **2.3 Jitter para Spawns e Despawns**
  - Aplicar um delay aleatório (jitter) de 0 a 10 minutos nas transições de turno para diluir o tráfego de entrada/saída de bots.

## 3. Inteligência de Combate e Tomada de Decisão (DNA AI)
- [x] **3.1 Lógica de Preservação e Fuga (Safety / Leash)**
  - Implementar decisão de SoE ou fuga quando HP < 20% proporcional ao atributo `preservacao`.
- [x] **3.2 Lógica de Cooperação e Socorro**
  - Implementar auxílio a aliados atacados na mesma área proporcional ao atributo `sociabilidade` / `altruismo`.

## 4. Toggles no `.ini` e Regra de Drop de PK
- [x] **4.1 Configuração `EnableFakeHunters` no `fakeplayers.ini`**
  - Adicionar chave booleana no `.ini` para atuar como toggle independente dos Fake Traders.
- [x] **4.2 Regra de Loot/Drop de PK**
  - Modificar a rotina `onDie` / `doDie` para gerar drop de itens apenas se `getKarma() > 0` (PK).

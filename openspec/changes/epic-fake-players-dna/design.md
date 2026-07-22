## Context

O L2Journey tem um sistema de bots (`FakePlayers`) que no passado funcionava à base de regras globais (todas no `fakeplayers.ini`), fazendo com que todo bot se comportasse da mesma maneira genérica. Com a Prova de Conceito (PoC), introduzimos a tabela `fake_players_profiles` e o `FakePlayerManager`. Agora, queremos evoluir isso para um sistema de "Mundo Vivo", onde cada bot terá seu próprio DNA comportamental de 0 a 100, capacidade de ignorar regras globais e um horário de atividade agendado (Turnos / Shifts).

## Goals / Non-Goals

**Goals:**
- Estruturar a tabela `fake_players_profiles` com um campo de metadados flexível (ex: JSON) para `behavior_overrides` e uma matriz de valores de 0 a 100.
- Implementar o Relógio Central no `FakePlayerManager` que dispara a cada 1 hora de tempo real do servidor para checar os turnos dos bots (MORNING, PRIME_TIME, etc.) e coordenar seus spawns/despawns.
- Implementar a nova regra universal: `FakePlayer` só gera drop de itens se o seu Karma atual indicar que ele é um PK (Karma > 0).

**Non-Goals:**
- Implementação de memória conversacional integrada com LLMs externos (isso será um épico separado, caso desejado).
- Modificação dos scripts XML de quests ou NPCs padrão da cidade; o foco são os bots dinâmicos.

## Decisions

- **Armazenamento do DNA:** As características numéricas (agressividade, preservação, ganância, rancor, etc.) serão colunas inteiras (TINYINT 0-100) em `fake_players_profiles` para facilitar queries do tipo `SELECT * FROM fake_players_profiles WHERE shift='PRIME_TIME' AND agressividade > 80`.
- **Armazenamento de Overrides:** Em vez de dezenas de colunas booleanas extras para cobrir o `fakeplayers.ini`, usaremos um campo `overrides` em formato JSON, onde chaves ausentes significam fallback para a regra global.
- **Relógio de Turnos:** O `FakePlayerManager` terá um cronjob interno que roda em intervalos (ex: 5 minutos ou sincronizado na hora cheia) para comparar a hora local do servidor com o Turno dos bots, forçando um SoE e Despawn naqueles cujo turno acabou, e spawnando os do turno atual.
- **Drop Apenas em PK:** Substituir as chamadas padrão no `onDie` do bot para verificar `if (bot.getKarma() > 0)` antes de permitir a tabela de drop gerar loot.

## Risks / Trade-offs

- **Risco: Performance na avaliação do JSON de overrides a cada ação da IA.**
  - *Mitigação:* Parse do JSON feito apenas no momento do `Spawn` (carregamento para a memória da instância `FakePlayer`), evitando parses no runtime do combate.
- **Risco: Spikes de Lag no fim dos Turnos (ex: às 18:00 todos deslogam juntos).**
  - *Mitigação:* Introduzir um `delay` randômico (jitter) de 0 a 10 minutos na ordem de despawn/spawn da troca de turnos.

## Context

O L2Journey necessita evoluir seu sistema de bots estáticos para um "Mundo Vivo". Inicialmente, planejou-se usar o Banco de Dados (`fake_players_profiles`) para persistir o DNA e os horários de bots lutadores (Hunters). No entanto, o design do sistema `fake-traders-engine` provou que instanciar bots como **Ghost Objects** puramente na Memória RAM (sem salvar no DB) é amplamente superior em performance e segurança. Assim, este design adapta a visão original do DNA para o novo paradigma modular.

## Architecture

- **FakeHunterManager**: Um singleton independente do `FakeTraderManager`. Possui um Tick Engine rápido (1-2 segundos) para gerir a movimentação pelo GeoEngine, casts de magias e verificação de HP/MP dos Hunters. Possui também um cronjob mais lento para gerir os Turnos (logins e logouts agendados).
- **XML Data Engine**: Substitui o banco de dados.
  1. `fake_hunters_dna.xml`: Define os perfis de personalidade numéricos (0-100 para Agressividade, Medo, Altruísmo) e listas de itens que eles carregam consigo.
  2. `fake_hunters_spawns.xml`: Define os locais de nascimentos, turnos (shifts), classes e link para o profile de DNA.
- **Toggles no `.ini`**: Duas chaves `EnableFakeTraders` e `EnableFakeHunters` garantindo que o Administrador tenha controle cirúrgico sobre a população do servidor.

## Decisions

- **Armazenamento de DNA em Memória**: O DNA será lido do XML e mapeado em um objeto `HunterDNA` que viverá dentro da instância do `FakePlayer` na memória RAM.
- **Ghost Objects Estritos**: Continuaremos herdando `FakePlayer extends Player` com o override no método `store()` para garantir zero persistência no MySQL. 
- **Relógio de Turnos com Jitter**: O `FakeHunterManager` forçará um SoE e Despawn naqueles cujo turno acabou. Para evitar lag spikes massivos (ex: 50 bots deslogando no mesmo milissegundo), adicionaremos um `delay` randômico (jitter) de 0 a 10 minutos na ordem de spawn/despawn.
- **Drop Apenas em PK**: Substituir as chamadas padrão no `onDie` do bot para verificar `if (bot.getKarma() > 0)` antes de permitir a tabela de drop gerar loot.

## Risks / Trade-offs

- **Risco: Performance do Tick Engine (Movimentação de Bots).**
  - *Mitigação:* Em vez de atualizar o pathfinding de todos os Hunters o tempo todo, o `FakeHunterManager` implementará uma "Otimização de Zona": os bots entram em estado de "Sleep" ou reduzem seu tick para 10 segundos quando não há jogadores reais no seu Grid/Zona.

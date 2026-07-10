## Context

Precisamos de um ambiente de laboratório em Gludio com 10 bots (2 traders, 8 hunters) para provar o conceito dos *Fake Players*. É crítico que esses bots possuam DNA persistente em banco de dados e que sua simulação não sobrecarregue a CPU caso o jogador real não esteja na região.

## Goals / Non-Goals

**Goals:**
- Validar a tabela `fake_players_profiles`.
- Criar a mecânica de Zone Listener ativando/desativando os bots baseado na presença do jogador na cidade de Gludio e adjacências (Death Pass, Ruins of Despair).
- Usar ciclos de tempo acelerados (1 hora) para testes.

**Non-Goals:**
- Implementação de IA avançada de combate (isso será em outro épico).
- Criação de centenas de bots simultâneos; ficaremos estritamente em 10 para o laboratório inicial.

## Decisions

- **Persistência SQL:** A tabela `fake_players_profiles` terá colunas para as características de DNA (agressividade 1-10, coragem 1-10), classe do bot, turno de spawn.
- **Zone Listener Otimizado:** Vamos associar a execução das threads da IA aos eventos de "EnterZone" e "ExitZone" das regiões chave. Se o player estiver fora dessas zonas, o estado dos bots é "dormindo" (suspenso) e apenas computado de forma abstrata.
- **Testes Acelerados:** Os schedulers que renovam o inventário dos traders rodarão com `delay = 1h` (ou equivalente no relógio in-game simulado).

## Risks / Trade-offs

- **Risco:** O uso de Zone Listeners muito granulares pode causar *stutters* (travamentos) quando o jogador cruzar a linha da zona, devido a muitos bots spawnando ao mesmo tempo.
  - *Mitigação:* Usar um *spawn* escalonado (staggered) em vez de instanciar todos de uma vez se necessário.

## Context

O L2Journey possui uma fundação de FakePlayers (instâncias de personagens simuladas pelo servidor) que utilizam a classe base `PlayerAI` e especializações como `FakeHunterAI`. Atualmente, essa IA é básica: foca apenas em auto-ataque e uso rudimentar de skills. Para que o servidor comporte bots interativos e sistemas complexos como troca de personagens dinâmicos (GTA V style), a IA precisa ser modularizada, permitindo árvores de decisão para suporte (healers), alocação tática de alvos e mecânicas de party (seguir um líder).

## Goals / Non-Goals

**Goals:**
- Criar um loop de decisão tático no `FakeHunterAI` ou em classes derivadas (`FakeHealerAI`, `FakeTankAI`).
- Implementar checagem automática e consumo de itens fundamentais (HP/MP/CP Potions e Soulshots).
- Permitir que a IA entenda comandos de party (seguir líder, curar membro do grupo, focar no alvo do líder).

**Non-Goals:**
- Substituir o controle do player humano. Apenas os bots autônomos/FakePlayers herdarão essa IA.
- Criar rotas automáticas complexas pelo mapa. A movimentação se restringirá ao campo de visão e à perseguição de alvos/líder.

## Decisions

**1. Modularidade da IA por Classes**
- *Decisão:* Criaremos classes específicas derivadas de `PlayerAI`, por exemplo, `FakeHealerAI`, ou uma interface de perfil de combate configurável no `FakePlayerProfile`.
- *Rationale:* Um Healer não deve focar em perseguir monstros, e sim observar o HP do líder. Ter lógicas separadas previne um código espaguete enorme no método `onEvtThink()`.

**2. Sistema de Task Scheduling para "Think"**
- *Decisão:* Aumentar a cadência do loop de raciocínio da IA (`onEvtThink` / `onIntention*`).
- *Rationale:* Os FakePlayers precisam ser ágeis para usar poções no milissegundo certo.

## Risks / Trade-offs

- **[Performance do Servidor]** Muitas instâncias de IA avaliando condições complexas (HP de toda a party, cooldown de skills) podem gerar TPS drop.
  - *Mitigação:* Usar cache de intenções e aumentar o delay do "Think" quando o FakePlayer não estiver em combate.
- **[Abuso de FakePlayers]** Jogadores podem criar bots excessivamente autônomos que quebrem a economia se não limitarmos como/quando a IA é ativada.
  - *Mitigação:* Restringir a criação de FakePlayers a administradores ou a um sistema estritamente regulado.

# SPEC: Master Ledger L2 Single Player

Este documento serve como o registro central de todas as funcionalidades, épicos e tarefas relacionadas à transformação do L2Journey em um projeto Single Player. Ele deve ser atualizado continuamente à medida que novas lógicas de IA ou sistemas são desenvolvidos.

## Legenda de Status
- `[ ]` Não Iniciado
- `[-]` Em Andamento
- `[x]` Concluído / Implementado

## Épico 1: Fundação Documental
- [x] Criar diretório `/docs`
- [x] Implementar Visão, Arquitetura, Casos de Uso, Modelo de Dados, Interfaces, Requisitos e Glossário
- [x] Estabelecer este SPEC.md como mestre

## Épico 2: Prova de Conceito (PoC) - Ecossistema de Fake Players (Gludio)

**Objetivo:** Validar a arquitetura de IA e o impacto no Game Server utilizando um laboratório controlado de 10 bots.

### Task Breakdown Sequencial:

**Fase 1: Persistência de Dados**
- [ ] Criar a tabela SQL `fake_players_profiles`.
- [ ] Adicionar colunas para DNA Comportamental: `agressividade` (1 a 10) e `coragem` (1 a 10).
- [ ] Adicionar colunas para `classe` e `turno` de jogo.
- [ ] Implementar a entidade correspondente no backend (Java) para realizar o carregamento destes dados.

**Fase 2: Otimização (Zone Listener)**
- [ ] Desenvolver a mecânica do `Zone Listener` no `FakePlayerManager`.
- [ ] Restringir a ativação: O servidor só processará o spawn dos bots caso o jogador principal esteja fisicamente em Gludio, Death Pass ou Ruins of Despair.
- [ ] Lógica de *suspend/sleep* para quando o jogador sair destas zonas adjacentes.

**Fase 3: Povoamento Inicial (10 Bots)**
- [ ] **Traders em Gludio:** Spawn de 2 Traders na cidade (1 com lista de itens fixa, 1 com itens variáveis).
- [ ] **Hunters em Death Pass:** Spawn de 4 Hunters focados no combate contra monstros agressivos.
- [ ] **Hunters em Ruins of Despair:** Spawn de 4 Hunters com foco em ruínas e monstros mortos-vivos.

**Fase 4: Ciclos de Teste Acelerado**
- [ ] Configurar cronograma de renovação de inventário dos Traders (Ciclo de 1 hora).
- [ ] Configurar turnos curtos de caça para os Hunters.
- [ ] Validar o uso do 'Scroll of Escape' para a cidade ao final do turno curto e a execução do 'despawn'.

## Épico 3: Expansão de IA (Futuro)
- [ ] Mapeamento dinâmico de party.
- [ ] Economia dinâmica baseada em suprimento/demanda nas outras cidades.

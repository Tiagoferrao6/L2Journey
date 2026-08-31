# Proposta: Customização de Classes (Cumulative Skills)

## Objetivo
Criar um sistema de "Subclasse Acumulativa" acessível para todos os jogadores do servidor. Esse sistema permitirá que o jogador adicione uma classe secundária *dentro* da sua classe atual (seja ela a Main Class ou uma Subclasse comum), garantindo acesso simultâneo às skills de ambas as classes.

## Regras de Negócio
1. **Desbloqueio via Quest**: O jogador precisará completar uma Quest customizada para habilitar o sistema. A missão consistirá em caçar o Raid Boss **Golkonda** para obter o item customizado **"Golkonda Horn"**. Somente entregando este item ao NPC a funcionalidade será destravada.
2. **Restrição de Raça**: A Subclasse Acumulativa **obrigatoriamente** deve pertencer à mesma raça da classe que a está recebendo.
   - *Exemplo:* Um Paladin (Humano) só pode escolher Warlord, Treasure Hunter, Sorcerer, etc. Não pode escolher Temple Knight (Elfo).
   - *Exemplo 2:* Se o jogador estiver logado na sua Subclasse de Phantom Ranger (Dark Elf), ele só poderá adicionar uma acumulativa de Dark Elf para aquele slot.
3. **Escopo**: Cada slot de classe (Main, Sub 1, Sub 2, Sub 3) pode ter sua própria Subclasse Acumulativa atrelada.

## Viabilidade Técnica
Analisando o núcleo do *L2Journey* (`Player.java`), o banco de dados e a engine já possuem suporte nativo à coluna `dual_class_id` na tabela `character_subclasses` (sistema herdado ou adaptado previamente, como vimos nos personagens `SilverTester` e `TitanTester`).

O que precisamos desenvolver:
1. **Script de Quest e Golkondas Alternativos**:
   - Criar o script da Quest de coleta do item customizado **"Golkonda Horn"**.
   - Criar **versões alternativas do Golkonda**, inspiradas no clássico servidor *Dragon-Network*, distribuídas da seguinte forma:

```text
┌────────────────────────────────────────────────────────────────────────┐
│                                                                        │
│  [Tier 1] Exiled Golkonda (Level 75)                                   │
│  ▶ Dificuldade: Solo / Duo (Fácil)                                     │
│  ▶ Localização: The Cemetery                                           │
│  ▶ Conceito: Uma versão mais fraca e exilada. Bate fraco, mas tem      │
│    muito HP. Chance menor de dropar o Horn (ex: 25%).                  │
│                                                                        │
│  [Tier 2] Original Golkonda (Level 79)                                 │
│  ▶ Dificuldade: Small Party (Médio)                                    │
│  ▶ Localização: Tower of Insolence (11º Andar) - Original              │
│  ▶ Conceito: O boss clássico intacto. Chance moderada do drop (50%).   │
│                                                                        │
│  [Tier 3] Infernal Golkonda (Level 80)                                 │
│  ▶ Dificuldade: Full Party (Difícil)                                   │
│  ▶ Localização: Monastery                                              │
│  ▶ Conceito: Cercado por anjos. Causa alto dano em área.               │
│    Chance garantida de drop (100% para 1 a 5 Horn).                    │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```
2. **Custom NPC / Village Master**: Criar a interface HTML e os *bypasses* (`bypass -h npc_%objectId%_addCumulativeSub`) para listar as classes permitidas (filtrando por raça) e gravar o `dual_class_id` no banco de dados.
3. **Validação da Engine de Skills**: Confirmar se o carregamento dinâmico de skills ao trocar de subclasse está entregando corretamente a árvore de habilidades da Main + Dual para jogadores normais, e garantir que a UI do cliente exiba as skills.

## Próximos Passos
Se a proposta for aprovada, passaremos para a fase de design técnico das tabelas de filtro de raça e a elaboração do enredo da Quest de desbloqueio.

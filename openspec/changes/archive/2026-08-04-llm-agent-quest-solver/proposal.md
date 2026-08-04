# Proposal: Automação de Quests & Mudança de Classe (Quest Solver Engine)

## Summary
Implementar um motor de raciocínio de Quests impulsionado por LLM para que os bots `PersistentFakePlayer` possam autonomamente aceitar, progredir e concluir quests do Lineage 2 (com foco especial nas Quests de Troca de Classe do nível 20 e nível 40).

## Motivation
Atualmente, bots normais precisam de rotas rígidas hardcoded para subir de nível. Para que um bot evolua de forma verdadeiramente orgânica do nível 1 ao 85 em um servidor Java de Lineage 2, ele precisa entender o diário de missões (Quest Log), conversar com NPCs de quest, viajar até os locais indicados, coletar itens de quest e realizar as mudanças de classe (ex: Human Fighter ➔ Warrior ➔ Warlord / Gladiator).

## Proposed Changes
- **Quest State Reflection API**: Expor o estado atual das quests ativas do personagem (`QuestState`, `QuestEnv`, itens de quest na bolsa) para o gerador de prompts da LLM.
- **Quest Decision Planner**: Permitir que a LLM consulte a enciclopédia de quests do jogo ou o Quest Log do personagem e retorne ações como `TALK_QUEST_NPC`, `HUNT_QUEST_MOB` e `CHANGE_CLASS`.
- **Class Change Automation**: Executar automaticamente as trocas de classe ao atingir os níveis 20, 40 e 76.

## Verification
- Teste Automatizado da Quest de 1st Class Transfer (ex: "Path of the Warrior" ou "Path of the Human Knight").
- Verificar se o bot fala com o Master em Gludin, vai até o local correto de mobs de quest e conclui a quest recebendo a prova de classe.

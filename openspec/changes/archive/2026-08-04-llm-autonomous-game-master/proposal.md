# Proposal: Game Master Autônomo / Storyteller (Eventos Dinâmicos)

## Summary
Implementar um agente Game Master (GM) autônomo baseado na Gemini API para orquestrar dinamicamente eventos narrativos ao vivo no L2Journey. O GM Autônomo poderá spawnar hordas de monstros temáticas, criar missões globais com recompensas no chat, invocar chefes de raid com lore personalizado e interagir com os jogadores como um "Dungeon Master" vivo.

## Motivation
Eventos em servidores privados de Lineage 2 costumam ser estáticos ou exigir a presença constante de um GM humano. Com um GM Autônomo operado por LLM, o servidor ganha um "narrador" 24/7 capaz de gerar micro-histórias, invasões surpresa e torneios dinâmicos de acordo com a movimentação e nível médio dos jogadores online.

## Proposed Changes
- **GM Orchestrator Engine (`AutonomousGMManager.java`)**: Serviço com privilégios de GM para spawnar NPCs, emitir anúncios globais (`Announcements.getInstance().announceToAll`), distribuir recompensas e monitorar o status do mundo.
- **Storyteller Event Templates**: Modelos de eventos dinâmicos (ex: "Invasão de Orcs em Gludio", "Caça ao Tesouro Desaparecido", "Desafio do Campeão da Arena").
- **Gemini Storyteller Prompts**: Prompt especializado em atuar como um Game Master / Narrador criativo de RPG de mesa.

## Verification
- Testar o comando de GM de início de evento narrativo e verificar os anúncios no chat global e o spawn correto dos NPCs do evento.
- Verificar se o evento se encerra automaticamente quando os objetivos definidos pelo GM forem atingidos pelos jogadores.

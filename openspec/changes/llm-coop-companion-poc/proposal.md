# Proposal: PoC do Agente Companheiro de IA Autônomo (Co-op Companion)

## Summary
Implementar a Prova de Conceito (PoC) de um Agente de IA Autônomo persistente (`PersistentFakePlayer`) controlado por modelo LLM local (Ollama) ou Gemini API. O bot iniciará como um novo personagem no nível 1, atuará como o parceiro de batalha dedicado do jogador humano ("Tiago"), evoluirá do nível 1 ao 40 realizando as quests de troca de classe (1st e 2nd Class Transfer para Paladin/Knight), operará 24/7 de forma síncrona em Party quando o humano estiver online ou em tarefas solo/missões quando offline, e salvará todo o progresso no banco MySQL.

## Motivation
Atualmente os bots do servidor são estáticos ou efêmeros. Esta PoC valida a criação do "Companheiro de Jogo Ideal": um bot autônomo inteligente que joga junto com o jogador humano, aprende com o histórico de combate, aceita ordens em linguagem natural via chat/PM e consulta o jogador em decisões estratégicas importantes.

## Proposed Changes
- **Companion State Machine (`CompanionState`)**: 3 modos de operação: `ACTIVE_COOP` (humano online em party), `ASSIGNED_MISSION` (executando ordem deixada pelo humano ao deslogar) e `AUTONOMOUS_SOLO` (farmando PvE e vendendo loots em NPCs).
- **Persistent Character Creation**: Inicialização dinâmica da conta e personagem no MySQL através de prompts de escolha da LLM.
- **Quest & Class Transfer Automation**: Leitura do estado das quests de Knight e Paladin e automação das trocas de classe no nível 20 e 40.
- **Interactive Chat & Consultations**: Comunicação via Whisper/Party chat para tirar dúvidas com o líder humano e reportar o progresso feito enquanto o humano esteve offline.

## Verification
- Validar criação do personagem na tabela `characters` do MySQL.
- Executar teste de login do jogador humano "Tiago", convite para party e verificação da transição do bot para o modo `ACTIVE_COOP`.
- Testar o envio de uma ordem de farm via PM antes de deslogar o humano e confirmar que o bot continua farmando no modo `ASSIGNED_MISSION`.
- Validar a conclusão da 2nd Class Transfer (Paladin) no nível 40.

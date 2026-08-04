# Proposal: Conexão do Agente Companheiro com LLM Local (Ollama / Llama 3)

## Summary
Integração do motor `LLMCompanionManager` ao serviço de inferência local Ollama (Llama 3 / Mistral) via requisições HTTP REST assíncronas em segundo plano. O bot responderá a mensagens privadas (PM/Whisper) e interações de Party em linguagem natural fluida e humanizada com sotaque gamer de Lineage 2, eliminando respostas robóticas de debug.

## Motivation
Atualmente o bot responde com frases estáticas de sistema ("Teleportado para sua posição!"). Para proporcionar a experiência de um parceiro de jogo autêntico ("Co-op Companion"), o bot precisa compreender mensagens livres enviadas pelo jogador humano e responder estrategicamente e naturalmente.

## Proposed Changes
- **LLM HttpClient (`LLMClient.java`)**: Cliente HTTP REST não-bloqueante para comunicação via API do Ollama (`http://localhost:11434/api/generate`).
- **Dynamic Context Prompting**: Injeção do estado atual do bot (nível, classe, HP/MP, localização e inventário) no prompt do sistema.
- **Natural L2 Gamer Persona**: Definição de personalidade Gamer BR com termos clássicos de Lineage 2 (ss, mob, cata, party, tank, farm).

## Verification
- Testar envio de PM livre pelo jogador humano (ex: "fala PaladinBot, bora catacumbas?") e validar resposta em português natural retornada pelo Ollama.

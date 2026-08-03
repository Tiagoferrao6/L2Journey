# Proposal: Socialização Nível RPG (Chat, Dicas, Mentores e Trash Talk)

## Summary
Dotar os bots LLM do L2Journey de capacidade natural de comunicação em linguagem humana nos canais de chat do jogo (Geral, Whisper/PM, Party, Clã, Shout/`!`). Os bots poderão dar dicas de jogo, tirar dúvidas de novatos como mentores, conversar sobre o mundo de Aden, festejar vitórias e fazer "trash talk" descontraído durante combates.

## Motivation
Servidores privados frequentemente parecem "desertos" ou sem vida social nos canais de chat. Com a integração da Gemini API / LLM local, os bots responderão dinamicamente em português (ou outro idioma configurado) mantendo a essência e o tom RPG de Lineage 2, criando uma experiência imersiva inédita.

## Proposed Changes
- **Chat Packet Handler & Router**: Capturar mensagens enviadas para o bot ou menções nos canais públicos e encaminhar para a fila de raciocínio da LLM.
- **Tone & Persona System**: Configuração XML de persona para cada bot (ex: "Paladino Honorável", "Dwarven Ganancioso", "Treta / Provocador", "Mentor Paciente").
- **Cooldown & Anti-Spam Guardrails**: Rate-limiting de mensagens para evitar spam nos canais públicos.

## Verification
- Teste de Whisper: Enviar PM "/whisper BotName Onde consigo Varnish?" e verificar se a resposta enviada pelo servidor é coerente com as dicas do jogo.
- Teste de Cooldown: Verificar se o bot respeita intervalo mínimo de 5s entre mensagens em canais públicos.

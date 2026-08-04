# Design: Conexão do Agente Companheiro com LLM Local (Ollama / Llama 3)

## Architecture Diagram

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         JOGADOR HUMANO ("Tiago")                            │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │ (PM / Whisper livre no chat)
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                    COMPANION MANAGER & PROMPT ENGINE                        │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • Captura a mensagem enviada ao PaladinBot                                 │
 │  • Formata o estado (Classe, Nível, Local, HP/MP, Itens)                    │
 │  • Envia a chamada HTTP POST assíncrona ao Ollama sem travar o GameServer   │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         OLLAMA LOCAL (Llama 3 API)                          │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • Processa a entrada e gera texto em linguagem natural Gamer BR            │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                    RESPOSTA ENVIADA VIA WHISPER AO CHAT                     │
 └─────────────────────────────────────────────────────────────────────────────┘
```

# Design: PoC Qwen 2.5 1.5B no WSL2 (CPU)

## Architecture Diagram

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         JOGADOR HUMANO ("Tiago")                            │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │ (PM / Whisper no Chat L2)
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         L2JOURNEY GAMESERVER (JAVA)                         │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • LLMCompanionManager (Monta estado do bot: HP, MP, Nível, Missão)         │
 │  • LLMClient.java (Dispara requisição POST JSON para http://localhost:11434)│
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │ (HTTP POST - modelo: qwen2.5:1.5b)
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                      OLLAMA SERVICE (WSL2 CPU INFERENCE)                    │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │  • Modelo: qwen2.5:1.5b (Q4_K_M ~1.1 GB)                                    │
 │  • Threads CPU: 4 núcleos Intel i5-1135G7                                  │
 │  • Latência de geração: ~40-60 tokens/seg (Tempo total < 1s)                 │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                                        ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                       RESPOSTA EXIBIDA VIA WHISPER                          │
 └─────────────────────────────────────────────────────────────────────────────┘
```

## Hardware & System Requirements
- **Processador**: Intel Core i5-1135G7 (4 núcleos / 8 threads)
- **RAM Alocada**: ~1.5 GB RAM para o Ollama + modelo
- **Plataforma**: WSL2 Ubuntu / Linux

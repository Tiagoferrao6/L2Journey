# Tasks: PoC Qwen 2.5 1.5B no WSL2 (CPU)

- [x] Executar o download do modelo `qwen2.5:1.5b` no Ollama local (`ollama pull qwen2.5:1.5b`) <!-- id: 0 -->
- [x] Atualizar o modelo padrão (`DEFAULT_MODEL = "qwen2.5:1.5b"`) em `LLMClient.java` <!-- id: 1 -->
- [x] Otimizar o prompt de sistema em `LLMCompanionManager.java` para máxima precisão com modelos de 1.5B <!-- id: 2 -->
- [x] Testar envio de PM e validar latência de resposta (< 1s) e consumo de RAM <!-- id: 3 -->

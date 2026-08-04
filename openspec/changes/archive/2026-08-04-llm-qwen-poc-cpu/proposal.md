# Proposal: PoC de Instalação, Conexão e Execução do Qwen 2.5 1.5B (Ollama CPU no WSL2)

## Summary
Implementar uma Prova de Conceito (PoC) para baixar, conectar e executar o modelo de linguagem ultra-leve `qwen2.5:1.5b` no Ollama rodando em modo CPU dentro do ambiente WSL2 (Intel i5-1135G7 + 16GB RAM). O objetivo é alcançar latência ultrarrápida (< 1s por resposta) e baixo consumo de memória (~1.5 GB RAM) no chat privado do bot companion.

## Motivation
Em máquinas de desenvolvimento sem placa de vídeo dedicada (GPU), modelos maiores (como 8B) geram respostas com latência de 5 a 8 segundos. O modelo `qwen2.5:1.5b` oferece excelente compreensão de comandos, alta fluência em Português BR e tempo de resposta quase instantâneo (< 500ms) em CPUs modernas, permitindo testes ágeis sem comprometer a memória do servidor Java e do cliente L2.

## Proposed Changes
- **Ollama Setup & Validation**: Garantir que o serviço Ollama esteja ativo no WSL2 e baixar o modelo `qwen2.5:1.5b` (`ollama pull qwen2.5:1.5b`).
- **Configuration & Integration (`LLMClient.java`)**: Configurar o `DEFAULT_MODEL = "qwen2.5:1.5b"` no `LLMClient.java` com timeout reduzido otimizado para inferência local CPU.
- **Prompt Adjustments for Small Models**: Ajustar o prompt de sistema em `LLMCompanionManager.java` com instruções altamente diretas para garantir máxima adesão do modelo de 1.5B à persona gamer do L2.

## Verification
- Executar `ollama list` no terminal WSL2 e confirmar a presença do `qwen2.5:1.5b`.
- Testar chamada REST assíncrona do GameServer ao Ollama.
- Enviar mensagem privada (PM/whisper) para o bot `PaladinBot` no jogo e validar resposta fluida em menos de 1 segundo.

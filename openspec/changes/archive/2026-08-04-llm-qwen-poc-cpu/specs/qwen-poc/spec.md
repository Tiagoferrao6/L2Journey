# Spec: PoC Qwen 2.5 1.5B no WSL2 (CPU)

## Requirements

### Requirement: Suporte ao Modelo Ultra-Leve Qwen 2.5 1.5B
The system MUST support sending inference requests to the local Ollama API using `qwen2.5:1.5b` for sub-second CPU inference responses.

#### Scenario: Envio de prompt para o Qwen 2.5 1.5B
- **GIVEN** o modelo `qwen2.5:1.5b` está instalado no Ollama local
- **WHEN** o jogador envia uma mensagem de whisper para o companheiro
- **THEN** o sistema gera a resposta em linguagem natural em menos de 1 segundo utilizando no máximo 1.5 GB de RAM.

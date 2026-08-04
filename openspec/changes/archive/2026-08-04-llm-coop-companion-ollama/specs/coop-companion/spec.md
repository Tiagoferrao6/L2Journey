# Spec: Conexão Ollama LLM Local

## Requirements

### Requirement: Comunicação Assíncrona com API Ollama Local
The system MUST send chat requests asynchronously to the local Ollama API endpoint (`http://localhost:11434/api/generate`) without blocking the GameServer thread loop.

#### Scenario: Envio de mensagem privada para o PaladinBot
- **GIVEN** o serviço Ollama está rodando localmente na porta 11434
- **WHEN** o jogador humano envia qualquer mensagem via PM para o "PaladinBot"
- **THEN** o sistema envia o prompt assincronamente e exibe a resposta gerada pela LLM no chat privado do jogador.

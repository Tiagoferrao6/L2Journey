## ADDED Requirements

### Requirement: Buffer Circular de Log por Bot (Bot Execution Trace)
The system MUST maintain a circular in-memory buffer (`BotExecutionTrace`) capturing the last 30 log entries and state transitions for each active AI Companion bot.

#### Scenario: Registro de evento de erro no buffer circular
- **WHEN** um bot enfrenta uma falha de navegação ou compra malsucedida
- **THEN** o sistema adiciona a mensagem de erro formatada ao `BotExecutionTrace` do bot correspondente descartando a entrada mais antiga se a capacidade (30) for excedida.

### Requirement: Disparo Autônomo de Diagnóstico Meta-Cognitivo
The system MUST invoke `LLMDiagnosticEngine` automatically when a bot accumulates 3 consecutive failures of the same category, submitting the `BotExecutionTrace` and `bot_capabilities.json` schema to the LLM.

#### Scenario: Detecção de padrão de falhas recorrentes
- **WHEN** um bot acumula 3 falhas consecutivas de navegação urbana ou compra
- **THEN** o `LLMDiagnosticEngine` compõe um prompt de análise contendo o histórico de logs e solicita um diagnóstico de intervenção ao modelo de linguagem.

### Requirement: Motor de Execução Dinâmica de JavaScript (JS Runtime)
The system MUST provide a GraalVM/JVM JavaScript script engine (`JavaScriptRuntimeEngine`) capable of loading, compiling, and executing `.js` script files from `data/scripts/ai_interventions/` in real time without restarting the GameServer.

#### Scenario: Execução instantânea de script de intervenção gerado por IA
- **WHEN** a LLM gera ou seleciona um script `unstick_bot.js` para destravar um bot
- **THEN** o `JavaScriptRuntimeEngine` executa o script injetando o contexto da instância do bot e aplicando as instruções de contingência sem interrupção do servidor.

### Requirement: Autonomia de Nível 2 para Criação e Edição de Scripts por IA
The system MUST allow the LLM Watchdog to generate new `.js` scripts or edit existing script templates under `data/scripts/ai_interventions/` to formulate novel multi-step recovery strategies for unhandled edge cases.

#### Scenario: IA cria um novo script de recuperação para falha inédita
- **WHEN** a LLM identifica uma condição de travamento inédita que não pode ser resolvida por uma única chamada de função
- **THEN** a IA escreve o código JavaScript correspondente na pasta `data/scripts/ai_interventions/` e dispara sua execução imediata para autorrecuperação do bot.

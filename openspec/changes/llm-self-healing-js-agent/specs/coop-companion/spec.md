## MODIFIED Requirements

### Requirement: Tomada de Decisão Cognitiva por Motor LLM (LLM Decision Planner Engine)
The system MUST provide a cognitive decision planner engine (`LLMTankerPlannerEngine`) and a diagnostic watchdog engine (`LLMDiagnosticEngine`) that send game state snapshots and `BotExecutionTrace` logs to the LLM (Qwen/Ollama), parsing JSON action decisions and executing dynamic JavaScript intervention scripts to maintain bot operational stability.

#### Scenario: Intervenção autônoma via script JS em caso de travamento
- **GIVEN** um bot Companion preso em um ciclo de repetição de erros
- **WHEN** o `LLMDiagnosticEngine` analisa o buffer de logs do bot
- **THEN** a IA seleciona ou gera um script JavaScript de contingência em `data/scripts/ai_interventions/` e executa a intervenção direta no bot.

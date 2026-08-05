## Why

Os bots de IA Companions podem encontrar situações imprevistas no jogo (estagnação de navegação, falhas de suprimentos, alteração de geodata, loops de IA). Para resolver travamentos sem depender de reinicializações do servidor ou novas compilações em Java, este projeto introduz uma arquitetura de **Diagnóstico por Logs + Nível 2 de Autonomia com Scripting Dinâmico em JavaScript**.

O sistema envia buffers circulares de log (`RingBuffer`) para um modelo LLM Watchdog, que analisa a causa raiz e pode **escrever ou modificar scripts JavaScript em tempo de execução** para executar intervenções diretas nos bots.

## What Changes

- **Buffer Circular de Logs por Bot (`BotExecutionTrace`)**: Registrador de histórico de intenções, falhas de navegação e retornos de compras em memória (últimas 30 linhas por bot).
- **LLM Diagnostic Watchdog Engine (`LLMDiagnosticEngine`)**: Motor de análise meta-cognitiva que detecta $N$ falhas consecutivas e envia o trace de logs + a API de capacidades para a LLM.
- **Ambiente de Execução Dinâmica de JavaScript (JavaScript Scripting Runtime)**: Integração com o motor JavaScript nativo da JVM (GraalVM ScriptEngine) permitindo compilação e execução instantânea de rotinas `.js` na pasta `data/scripts/ai_interventions/`.
- **Autonomia Nível 2 (Geração Dinâmica de Scripts)**: A LLM pode responder tanto com invocações de função estruturadas (Function Calling) quanto gerar novos arquivos `.js` contendo sequências completas de intervenção (ex: alterar cooldowns, forçar rotas alternativas, emitir avisos de chat).

## Capabilities

### New Capabilities
- `companion-self-healing-js`: Define requisitos para diagnóstico autônomo baseado em logs, runtime de scripts JavaScript e intervenção de Nível 2 para bots de IA.

### Modified Capabilities
- `coop-companion`: Atualiza os requisitos do ciclo de vida dos AI Companions para suportar rastreamento de logs e execução de scripts de contingência.

## Impact

- `com.l2journey.gameserver.managers.LLMCompanionManager`: Adição de `BotExecutionTrace` e gatilho de diagnóstico.
- Novo Gerenciador: `com.l2journey.gameserver.managers.LLMDiagnosticEngine` para consulta meta-cognitiva e parser de scripts.
- Novo Runner: `com.l2journey.gameserver.scripting.JavaScriptRuntimeEngine` com isolamento sandbox básico para segurança.
- Diretório de Scripts: `data/scripts/ai_interventions/` para armazenamento dos scripts gerados/modificados pela IA.

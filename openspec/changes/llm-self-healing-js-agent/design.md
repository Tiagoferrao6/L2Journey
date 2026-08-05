## Context

Bots de IA enfrentam situações complexas no jogo (bugs de geodata, falta inesperada de recursos, falhas de pathfinding, respostas atípicas de NPCs). Erros repetitivos podem travar o bot em loops infinitos.

Este design especifica um **Watchdog de Diagnóstico Meta-Cognitivo de Nível 2** capaz de receber traces de logs dos bots e gerar/executar scripts JavaScript dinâmicos para autorrecuperação e intervenção direta.

## Goals / Non-Goals

**Goals:**
- Implementar `BotExecutionTrace` (buffer circular de 30 linhas de log por bot).
- Criar `LLMDiagnosticEngine` para consultar o modelo LLM quando $N \ge 3$ falhas forem detectadas.
- Integrar `JavaScriptRuntimeEngine` usando a biblioteca de ScriptEngine da JVM/GraalVM para compilar e executar arquivos `.js` em tempo real.
- Permitir que a LLM crie e salve scripts na pasta `data/scripts/ai_interventions/` (Nível 2 de Autonomia).

**Non-Goals:**
- Não permitir que scripts JS acessem reflexão ilimitada da JVM sem checagem de escopo (aplicar sandbox de segurança restringindo o acesso aos métodos públicos de `FakePlayer` e `LLMCompanionManager`).

## Decisions

### 1. Dupla Camada de IA (Tactical vs. Diagnostic Watchdog)
- **Decisão**: Separar o planejador tático rápido (`LLMTankerPlannerEngine`, 1-3s) do motor de diagnóstico (`LLMDiagnosticEngine`, ativado apenas sob falhas recorrentes).
- **Alternativas consideradas**: Enviar todo o log do bot em cada tick tático de 3s. Rejeitada pois causaria sobrecarga massiva e estouraria os timeouts da API de LLM local.

### 2. Formato de Scripts em JavaScript (GraalVM ScriptEngine)
- **Decisão**: Utilizar JavaScript para a geração de scripts dinâmicos pela IA. O motor de script expõe a variável `bot` e os métodos de intervenção permitidos.
- **Alternativas consideradas**: Groovy, Python (Jython). Rejeitada pois a maioria dos LLMs possui maior taxa de acerto e sintaxe limpa na geração autônoma de JavaScript ES6.

### 3. Estrutura da Pasta de Intervenções
- **Decisão**: Armazenar os scripts em `data/scripts/ai_interventions/`. O `LLMDiagnosticEngine` pode carregar scripts pré-existentes ou gravar novos arquivos gerados pela LLM nessa pasta antes da execução.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    ESTRUTURA DO MOTOR DE SCRIPTING JS                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  LLM Diagnostic Engine ──► Gera/Atualiza "data/scripts/ai_interventions/..."│
│                                 │                                           │
│                                 ▼                                           │
│                   JavaScriptRuntimeEngine.eval()                            │
│                                 │                                           │
│                                 ▼                                           │
│                   Instância do FakePlayer (bot)                             │
│                   - bot.clearAIIntention()                                  │
│                   - bot.setCooldown("GO_TO_SHOP", 120)                      │
│                   - bot.teleToLocation(x, y, z)                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Risks / Trade-offs

- **[Risk] Erros de sintaxe ou exceções no JS gerado pela IA** → **Mitigação**: O `JavaScriptRuntimeEngine` captura `ScriptException` e faz rollback automático para uma ação segura por padrão (ex: `clearAIIntention` e teleport para vila).
- **[Risk] Scripts criarem novos loops infinitos** → **Mitigação**: Todo script JS executado possui tempo limite de execução (timeout de 2 segundos de CPU) e é descartado após a conclusão da intervenção.

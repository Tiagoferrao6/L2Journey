## 1. Trace de Logs e Diagnóstico LLM

- [ ] 1.1 Criar a classe `BotExecutionTrace.java` para gerenciar um buffer circular de memória (30 linhas de log/eventos por bot).
- [ ] 1.2 Integrar o registro de eventos no `LLMCompanionManager.java` (falhas de rota, falhas de compra, mudanças de intenção).
- [ ] 1.3 Criar `LLMDiagnosticEngine.java` para formatar prompts de diagnóstico com logs e catálogo de capacidades quando 3 falhas consecutivas forem detectadas.

## 2. Motor de Scripting Dinâmico JavaScript (Nível 2)

- [ ] 2.1 Criar a classe `JavaScriptRuntimeEngine.java` integrando `javax.script.ScriptEngineManager` (GraalVM JS).
- [ ] 2.2 Implementar sandbox e injeção do objeto `bot` e utilitários no contexto de execução do script.
- [ ] 2.3 Criar a estrutura de diretórios `data/scripts/ai_interventions/` e o gravador/carregador dinâmico de scripts JS gerados pela LLM.
- [ ] 2.4 Implementar tratamento de exceções de script e fallback de segurança em caso de erro de sintaxe.

## 3. Validação e Testes

- [ ] 3.1 Criar testes em `LLMDiagnosticEngineTest.java` simulando a injeção de logs de travamento e a geração/execução de script JS de intervenção.
- [ ] 3.2 Simular bot retido em travamento por 3 ticks consecutivos e verificar se o `LLMDiagnosticEngine` compila e executa o script de autorrecuperação com sucesso.

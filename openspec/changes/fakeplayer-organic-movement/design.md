## Context

A movimentação de bots no Lineage 2 deve emular perfeitamente um cliente. Além da emulação básica, o bot precisa de inteligência logística. Introduzimos aqui o suporte a NavMesh (Malha de Navegação) gerenciável externamente via QGIS (usando Polígonos para Zonas de Caça e Linhas para Rotas de Viagem) e uma Árvore de Decisão baseada em economia (Tempo vs Adena).

## Goals / Non-Goals

**Goals:**
- Prover um mecanismo de gravação in-game e exportação de rotas (`.geojson`) para edição no QGIS.
- Suportar Zonas de Caça via Polígonos: A IA limitará seus movimentos aleatórios estritamente dentro da geometria do polígono.
- Implementar a Matriz de Decisão: A IA escolhe a rota mais barata (Adena) se tiver capital, ou a rota a pé (Tempo/Linha) se estiver pobre.
- Simular Delays Humanos na interação com GKs (1000ms a 2000ms entre janelas).

**Non-Goals:**
- Mudar a engine de Geodata original do L2. Apenas criaremos a camada de NavMesh superior.

## Decisions

**1. QGIS e NavMesh**
- *Decisão:* Adicionaremos comandos de GM `//admin_rec_path` e `//admin_stop_rec` que salvam pontos em formato KML/GeoJSON.
- *Rationale:* Reduz o tempo de configuração de zonas de caça de semanas para horas usando softwares geográficos padrão.

**2. Matriz de Decisão Econômica**
- *Decisão:* Antes de viajar, a IA calcula `(Custo Adena da GK)` e verifica o saldo atual. Se o saldo for insuficiente, ela altera seu estado para `HUNTING_MODE` na zona mais próxima até farmar o valor.
- *Rationale:* Torna a IA parte viva e fundamental da economia do servidor, sendo obrigada a farmar para viajar.

**3. Simulação de Humano em GKs**
- *Decisão:* A Action Queue do bot forçará um `Thread.sleep` (assíncrono) ou usará o agendador de tarefas nativo para atrasar entre 1000ms e 2000ms a confirmação de Bypass.
- *Rationale:* Evita detecção por sistemas anti-bot que buscam cliques em menos de 50ms.

## Risks / Trade-offs

- **[Complexidade Geométrica]** Checar se a coordenada (X,Y) está dentro de um Polígono pode ser custoso se feito a cada milissegundo.
  - *Mitigação:* O cálculo de intersecção só ocorrerá quando o bot calcular a próxima coordenada alvo (a cada 3~5 segundos), e não a cada tick de movimento.

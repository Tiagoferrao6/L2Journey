## 1. Módulo NavMesh e QGIS (Admin)

- [x] 1.1 Criar o AdminCommand `AdminNavMesh` para os comandos `//admin_rec_path` e `//admin_stop_rec`
- [x] 1.2 Implementar a função que converte o registro de rotas do GM em um arquivo `.geojson` (suporte a LineStrings) na pasta `data/navmesh/`
- [x] 1.3 Criar o Parser `NavMeshManager` que lê os GeoJSONs na inicialização do servidor e armazena Polígonos e Linhas em memória

## 2. Motor Orgânico de Navegação

- [x] 2.1 Adicionar checagem matemática de Polígonos: Quando o bot estiver em `HUNTING_MODE`, validar se o alvo de perseguição está contido no Polígono designado
- [x] 2.2 Integrar o Anti-Stuck: Salvar `_lastX`, `_lastY` e contador `_stuckTicks`. Após 3 falhas de distância < 10, aplicar desvio aleatório

## 3. Matriz de Decisão Econômica

- [x] 3.1 Criar o `TeleportGraph`, mapeando o custo de Adena de bypasses clássicos
- [x] 3.2 Antes do bot viajar, somar o custo total. Se `_player.getAdena() < Custo Total`, engatilhar evento de falha financeira
- [x] 3.3 Construir o fallback: Se falha financeira, o bot altera o estado para buscar a Zona de Caça mais próxima

## 4. Emulação Humana (Teleportes e Delays)

- [x] 4.1 Modificar a IA para caminhar até o NPC da Gatekeeper via Linhas (LineStrings)
- [x] 4.2 Inserir na Action Queue um `Thread.sleep` (ou agendador ThreadPool) de 1000~2000ms simulando a leitura da janela HTML
- [x] 4.3 Acionar o envio de `RequestBypassToServer` após o delay
- [x] 4.4 Implementar uso do SOE (ID 736) disparando o cast original (sem burlar o tempo de animação)

## 5. Validação Manual e Rotas de Teste

- [ ] 5.1 Testar Rota a pé (Curta): Ordenar o bot a ir do Portão Norte de Giran até o Portão Sul
- [ ] 5.2 Testar Rota a pé (Média): Ordenar o bot a ir de Giran até Dragon Valley a pé (Validando a malha/NavMesh fora da cidade)
- [ ] 5.3 Testar Rota via Teleporte (Longa): Ordenar o bot na praça de Giran a ir até a praça de Gludio (Validando múltiplas baldeações e delays de cliques em GKs)

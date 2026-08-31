## ADDED Requirements

### Requirement: Gravação e Importação de GeoJSON (QGIS)
O servidor DEVE possuir comandos administrativos para iniciar e parar a gravação de percursos a pé. Os percursos DEVERÃO ser exportados nativamente num formato legível para softwares de geoprocessamento (QGIS), e o servidor DEVE ler arquivos com Linhas (Safe Routes) e Polígonos (Hunting Zones).

#### Scenario: Gravação Manual de Rota Segura
- **WHEN** um administrador digita `//admin_rec_path Dion_Execution` e caminha até o local
- **THEN** o servidor registra pontos a cada curva/500 units e exporta o arquivo para a pasta de dados do bot

### Requirement: Caça Limitada por Polígono
Quando designado para caçar, o FakePlayer NÃO DEVE ultrapassar os limites geométricos do Polígono de Caça definido na sua rotina.

#### Scenario: Contenção de Bot
- **WHEN** o bot persegue um monstro que corre para fora da zona do Polígono de Caça
- **THEN** o bot abandona a perseguição na fronteira invisível e retorna para a zona interna do Polígono

### Requirement: Testes Práticos de Roteamento a Pé
A engine DEVE ser validada usando duas rotas manuais específicas para provar a eficiência de curta e média distância usando as malhas (NavMesh) ou o sistema de MoveTo nativo.

#### Scenario: Rota Curta (Intra-cidade)
- **WHEN** a IA é instruída a ir do Portão Norte de Giran até o Portão Sul de Giran a pé
- **THEN** ela percorre a rota orgulhosamente contornando os obstáculos e o centro da cidade sem colidir

#### Scenario: Rota Média (Inter-região)
- **WHEN** a IA é instruída a ir do centro de Giran até Dragon Valley a pé
- **THEN** ela utiliza a malha de Waypoints pré-definida para se guiar pelas estradas sem tentar cortar caminho por montanhas intrasponíveis

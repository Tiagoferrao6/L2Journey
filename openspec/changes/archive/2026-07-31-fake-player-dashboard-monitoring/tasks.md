# Tasks: Dedicated FakePlayer Live Monitoring Tab in GM Dashboard

## 1. Backend Java Endpoints (`WebAPIManager.java`)
- [x] **1.1 Handler de Listagem (`GET /api/admin/fakeplayers/list`)**
  - Coletar status, nível, classe, HP/MP, coordenadas X,Y,Z, Zona e Alvo dos Fake Hunters & Fake Traders.
- [x] **1.2 Handler de Ações GM (`POST /api/admin/fakeplayers/action`)**
  - Implementar ações `TELEPORT_GM`, `DESPAWN` e `FORCE_RETREAT`.

## 2. Frontend UI (`web/index.html`)
- [x] **2.1 Nova Aba `🤖 FakePlayers` no Painel GM**
  - Criar aba e layout no HTML5.
- [x] **2.2 Cards de Métricas & Filtros de Busca**
  - Adicionar resumo de estado da tropa de bots e filtros de busca por nome/zona.
- [x] **2.3 Tabela de Monitoramento de Fake Players**
  - Renderizar linhas com coordenadas (X, Y, Z), barras de HP/MP, tag de estado e botões de ação.

## 3. Validação & Testes
- [x] **3.1 Validação de API REST**
  - Validar payload JSON e autorização Bearer.
- [x] **3.2 Validação E2E no Navegador**
  - Testar atualização em tempo real no Dashboard.

# Tasks: Real-Time Web Dashboard & GM Control Panel

## 1. Backend REST API em Java (`WebAPIManager`)
- [x] **1.1 Configuração do `server.ini`**
  - Adicionar `EnableWebAPI = True`, `WebAPIPort = 8080` e `WebAdminToken = secret123`.
- [x] **1.2 Servidor HTTP Base (`WebAPIManager.java`)**
  - Instanciar `com.sun.net.httpserver.HttpServer` em thread dedicada com suporte a CORS e autenticação por Bearer Token.
- [x] **1.3 Endpoints Públicos (`/api/status`, `/api/economy`, `/api/pvp`, `/api/raids`)**
  - Métricas de Uptime, População (Reais vs. Bots), Lojas em Gludio, Ranking PvP/PK e Status dos Bosses.
- [x] **1.4 Endpoint de Hot-Reload de Rates (`POST /api/admin/rates`)**
  - Alteração ao vivo de EXP, SP, Adena e Drop Rates em memória (`Config.java`).
- [x] **1.5 Endpoint de Gerenciamento do Módulo FakePlayers (`POST /api/admin/fakeplayers`)**
  - Alternar On/Off e forçar reload dos XMLs de Fake Traders e Hunters.
- [x] **1.6 Endpoint de Gerenciamento do Alt+B (`POST /api/admin/community`)**
  - Reload e atualização dos produtos/preços do Community Board.
- [x] **1.7 Endpoint de Monitoramento de Chat e Players Reais (`/api/admin/chat` & `/api/admin/players`)**
  - Fila circular de chat em tempo real e lista detalhada de players humanos online com ações de GM (Kick, Message, Teleport).

## 2. Frontend Web Dashboard & Painel GM (HTML5 / Vanilla JS / CSS)
- [x] **2.1 Estrutura da Interface Pública (`index.html`)**
  - Visualização em Dark Theme com estética premium (Glassmorphism e acentos Dourados/Azuis).
- [x] **2.2 Cards de Status ao Vivo & Tabela Interativa de Mercado**
  - Painéis dinâmicos de uptime, saúde do servidor e gráfico de distribuição da população.
- [x] **2.3 Aba Protegida: Painel do GM (Admin Panel)**
  - Modal de autenticação por chave de acesso/token GM.
- [x] **2.4 Modais de Controle de Rates, Fake Players e Alt+B**
  - Formulários dinâmicos com sliders e botões de acionamento imediato (*Apply Rate*, *Reload Traders*, *Toggle Hunters*).
- [x] **2.5 Widget de Chat em Tempo Real & Lista de Players Reais**
  - Terminal de chat ao vivo filtrável por canal e tabela de players ativos com botões de ação rápida.

## 3. Infraestrutura & Docker
- [x] **3.1 Exposição de Porta no `docker-compose.yml`**
  - Expor a porta `8080:8080` do `gameserver`.
- [x] **3.2 Validação E2E**
  - Testar requisições do Dashboard Web público e ações administrativas do Painel GM em ambiente local.

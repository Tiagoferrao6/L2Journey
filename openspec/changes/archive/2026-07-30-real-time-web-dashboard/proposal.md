## Why

Para que os jogadores e administradores do L2Journey acompanhem a saúde do servidor, a população ativa (jogadores reais vs. bots), a economia das vilas (lojas de Fake Traders) e o status do mundo (Raid Bosses, Siege e PvP Top 10) sem precisar estar logados no jogo, precisamos de uma interface Web em tempo real.

Além da visualização pública, o Administrador (GM) precisa de um **Painel de Controle GM** local para alterar configurações do servidor ao vivo (Rates, Fake Players, Lojas Alt+B) e monitorar chats e jogadores humanos ativos sem a necessidade de reiniciar o GameServer.

Como os Fake Players e Fake Traders operam no modelo **Ghost Objects** (existindo puramente na memória RAM do GameServer e não gravados em banco MySQL), a solução ideal é um servidor HTTP/REST API leve embutido diretamente no GameServer, servindo dados ao vivo para um Dashboard Web completo.

## What Changes

- **Embedded Web API Server (`WebAPIManager`)**:
  - Mini servidor HTTP/REST leve (usando `com.sun.net.httpserver.HttpServer`) embutido no GameServer (porta configurável, ex: `8080`).
  - **Endpoints Públicos (Read-Only)**:
    - `/api/status`: Uptime, status LS/GS, contagem de Players Reais, Fake Hunters e Fake Traders online, uso de CPU/RAM.
    - `/api/economy`: Lista de lojas ativas (WTS/WTB/Craft) por cidade e volume de mercado.
    - `/api/pvp`: Top 10 PvP, Top 10 PK e estatísticas em tempo real.
    - `/api/raids`: Estado dos Raid Bosses (Vivo/Morto) e status dos Castelos/Sieges.
  - **Endpoints de Administração GM (Protegidos por Token/Senha)**:
    - `/api/admin/rates`: Consulta e alteração ao vivo de Rates (EXP, SP, Adena, Drop).
    - `/api/admin/fakeplayers`: Controls on/off, ajuste de densidade e reload dos XMLs de Fake Traders e Hunters.
    - `/api/admin/community`: Reload e gerenciamento de itens/preços do Alt+B (Community Board).
    - `/api/admin/chat`: Stream/Feed ao vivo dos canais de chat do jogo (Global, Trade, Shout, PMs).
    - `/api/admin/players`: Lista detalhada de players humanos online com ações de GM (Kick, Teleport, Warn).
- **Frontend Dashboard Web & Painel GM**:
  - Interface web responsiva em Dark Theme com estética premium (Glassmorphism, Gold Accents, gráficos dinâmicos).
  - Aba de **Painel GM** protegida por login/token para administração local do servidor.

## Capabilities

### New Capabilities
- `embedded-web-api`: Servidor HTTP leve rodando no GameServer fornecendo endpoints JSON públicos e administrativos.
- `realtime-web-dashboard`: Interface gráfica web para visualização pública e painel de controle GM.
- `gm-live-config-control`: Mecanismo de hot-reload de rates, fake players e Alt+B em memória RAM.

### Modified Capabilities
- `server-configuration`: Novas chaves no `server.ini` (`EnableWebAPI = True`, `WebAPIPort = 8080`, `WebAdminToken = secret123`).

## Impact

- **Segurança**: Endpoints administrativos usam autenticação por Bearer Token/Header HTTP pré-configurado no `server.ini`.
- **Performance**: O WebAPIManager roda em thread dedicada e utiliza cache curto (2s) para requisições de leitura pública.
- **Portas Docker**: Exposição da porta configurada (ex: `8080:8080`) no `docker-compose.yml`.

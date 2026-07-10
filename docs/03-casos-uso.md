# Casos de Uso e Ciclo de Vida da Sessão

## Ciclo de Vida Principal (Jogador)
O servidor opera ao redor da presença do jogador real.

### 1. Login e Detecção de Zona
- O jogador loga no servidor.
- O `FakePlayerManager` verifica em qual região o jogador se encontra (Zone-Listener).
- **Ação**: O Manager carrega e "acorda" os Fake Players daquela região específica (ex: Gludio), poupando processamento de regiões vazias.

### 2. Interações Comerciais (Traders)
- O jogador entra na cidade (Gludio).
- **Visão**: Vê diversos Traders sentados com lojinhas (Private Store).
- **Interação**: O jogador clica no Trader e compra/vende itens.
- **Ciclo Oculto**: A cada 24-72h no jogo, os Traders atualizam seus estoques baseados em um Trade-Cycle invisível, simulando uma economia real.

### 3. Progressão de Caça (Hunters)
- O jogador sai para caçar na região (ex: Ruins of Agony).
- **Visão**: O jogador encontra Hunters caçando ativamente.
- **Interação**: Hunters podem formar partys, podem atacar monstros, ou podem fugir caso o HP caia (baseado no peso de Coragem/DNA).

### 4. Eventos e Sieges (Futuro)
- Fake Players avançados poderão ser programados para se agrupar e participar de Castle Sieges.
- A presença do jogador na Siege acionará a ativação destas lógicas de alta intensidade.

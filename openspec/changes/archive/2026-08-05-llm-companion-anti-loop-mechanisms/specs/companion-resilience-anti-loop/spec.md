## ADDED Requirements

### Requirement: Trava de Re-entrância de Navegação em Cidade
The system MUST ignore new navigation route requests for a bot that is actively traversing an urban route in `TownWaypointMeshManager` until the `onComplete` callback fires or the route is cancelled.

#### Scenario: Bloqueio de reinício de rota ativa
- **WHEN** um bot recebe uma ordem de navegação para a loja e uma nova chamada de navegação é realizada 3 segundos depois enquanto o bot ainda caminha
- **THEN** o gerenciador de waypoints ignora a segunda requisição e permite que o bot conclua a rota em andamento sem resetar para o nó inicial.

### Requirement: Cooldown de Ações com Falha por Adena Insuficiente
The system MUST enforce a backoff cooldown period of at least 60 seconds in `BuyListExecutingEngine` and `LLMCompanionManager` before re-attempting a shop route for a bot that failed a purchase due to insufficient Adena.

#### Scenario: Aplicação de cooldown após falha de compra
- **WHEN** o bot executa `executePurchase` e o saldo de Adena é inferior ao custo total necessário
- **THEN** o sistema registra a falha, bloqueia o gatilho `needsConsumableReplenishment` para aquele bot durante 60 segundos e redireciona o bot para caça em `FARM_ZONE`.

### Requirement: Compras Proporcionais com Saldo Disponível
The system MUST dynamically calculate the quantity of Soulshots and Health Potions to purchase based on the bot's current Adena balance when partial funds are available.

#### Scenario: Compra parcial com saldo reduzido
- **WHEN** o bot possui menos de 7.000 Adena mas possui Adena suficiente para um pacote menor (ex: 50 shots)
- **THEN** o engine reduz a quantidade adquirida para se adequar ao saldo disponível sem lançar erro de Adena insuficiente.

### Requirement: Micro-Evasão de Geodata para Bot Retido
The system MUST detect movement stagnation ($\Delta X, \Delta Y < 30$ in 3 consecutive position checks) and apply a short lateral evasion vector before triggering direct teleport fallbacks.

#### Scenario: Execução de micro-passo evasivo em colisão
- **WHEN** o bot tenta caminhar até o próximo waypoint mas permanece na mesma posição por 3 checagens de movimento consecutivas
- **THEN** o sistema aplica um deslocamento lateral curto para contornar o obstáculo de geodata.

## ADDED Requirements

### Requirement: Matriz de Decisão de Viagem (Custo)
Antes de usar o Grafo de Gatekeepers, o bot DEVE avaliar seu saldo de adena contra a estimativa do percurso.

#### Scenario: Farm Pré-Viagem
- **WHEN** o bot decide ir de Giran para Aden, mas não possui Adena suficiente para os Teleportes
- **THEN** ele interrompe a viagem, transita para `HUNTING_MODE` na zona segura mais próxima de Giran, farma a quantia necessária e então retoma a viagem

### Requirement: Delay Humano na Gatekeeper
O bot NÃO DEVE emitir Bypasses no mesmo milissegundo em que chega no Range do NPC. Ele DEVE simular o tempo de leitura do HTML do L2.

#### Scenario: Uso Orgânico da GK
- **WHEN** o bot atinge a GK de Gludio
- **THEN** ele pausa seu movimento, aguarda um tempo randômico entre 1200ms e 2000ms e apenas então emite o `RequestBypassToServer` para o destino

### Requirement: Testes Práticos de Roteamento de Teleporte
A engine de Grafo de Teleporte DEVE ser testada exaustivamente numa rota longa (cross-city) englobando múltiplas baldeações e verificando se os delays de cada pulo são respeitados.

#### Scenario: Rota Longa via GK (Giran a Gludio)
- **WHEN** a IA é instruída a ir da Praça de Giran para a Praça de Gludio e tem adena suficiente
- **THEN** o bot caminha até a GK de Giran, espera o delay, manda bypass para Dion, aparece em Dion, caminha até a GK de Dion, espera o delay, manda bypass para Gludio, concluindo a viagem na praça de Gludio

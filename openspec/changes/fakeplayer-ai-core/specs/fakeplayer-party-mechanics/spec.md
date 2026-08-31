## ADDED Requirements

### Requirement: Leader Tracking
O FakePlayer DEVE conseguir memorizar um alvo aliado como sendo seu "Líder", e seguir sua posição de forma passiva fora de combate.

#### Scenario: Seguindo o Líder
- **WHEN** o líder do grupo se move para uma distância superior a 150 unidades
- **THEN** o FakePlayer ativa a ação de movimento para a coordenada do líder

### Requirement: Assist Target
Quando operando em modo "Assist", o FakePlayer DEVE adquirir como alvo primário a mesma criatura que seu Líder estiver atacando.

#### Scenario: Focando o monstro
- **WHEN** o Líder inicia um ataque contra o Poring
- **THEN** o FakePlayer imediatamente adquire o Poring como alvo e inicia seu loop ofensivo

### Requirement: Heal Priorities
Se o FakePlayer estiver operando sob a IA de Healer, ele DEVE focar curas críticas no Líder ou membros da Party acima da sua própria segurança, se a vida deles estiver em perigo.

#### Scenario: Risco Iminente
- **WHEN** o líder da Party está com 20% de HP e o Healer está com 50% de HP
- **THEN** o Healer usa a skill de Greater Heal no líder antes de curar a si mesmo

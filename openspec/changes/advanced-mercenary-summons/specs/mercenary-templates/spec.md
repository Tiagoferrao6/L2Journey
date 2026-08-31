## ADDED Requirements

### Requirement: Customização XML de Mercenários
O servidor DEVE fornecer um arquivo de configuração (ex: `data/stats/npcs/mercenaries.xml`) onde os administradores podem definir novos templates de mercenários. Um template DEVE suportar a definição de `displayId` (aparência base), `weaponId` (arma equipada, se a aparência for de player) e skills ativas/passivas.

#### Scenario: Carregamento do Template de Healer
- **WHEN** o servidor carrega as configurações na inicialização
- **THEN** ele parseia o template de ID 90001 e o registra como um "Mercenary Healer", pronto para ser vinculado a um item de evocação.

### Requirement: Equipamento Visível
Se o Mercenário possuir um `displayId` correspondente a um char base (Humano, Elfo, Orc), o sistema DEVE permitir e forçar o envio de pacotes que equipem armas e armaduras visualmente nele, semelhante aos jogadores e NPCs guardas.

#### Scenario: Visualizando a Arma
- **WHEN** o jogador evoca um Mercenário Elfo com `weaponId=65` (Red Crescent)
- **THEN** o cliente do jogo renderiza a elfa segurando a arma apropriada

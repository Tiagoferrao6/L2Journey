## ADDED Requirements

### Requirement: Interface de Pet/Summon
O sistema DEVE classificar o Mercenário como um `L2Summon`, garantindo que o cliente do jogo exiba automaticamente a Pet Action Window (com os ícones de Atacar, Parar, Seguir e Recolher Itens) quando o Mercenário for evocado.

#### Scenario: Controle Tático
- **WHEN** o jogador clica no botão "Atacar" na barra de ações do Pet
- **THEN** o Mercenário avança até o alvo atual do jogador e inicia combate usando seus parâmetros definidos

### Requirement: Rejeição de Dupla Invocação
O sistema NÃO DEVE permitir que um jogador evoque um Mercenário se ele já possuir um Pet, Cubo ou outro Summon ativo, a não ser que uma configuração explícita de servidor permita "Multiple Summons".

#### Scenario: Prevenção de Abuso
- **WHEN** o jogador tenta usar um "Contrato de Mercenário" já tendo um Wolf ativo
- **THEN** o servidor cancela a invocação e envia uma mensagem de erro ("You already have a pet summoned.")

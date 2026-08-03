# Spec: Co-op Companion AI Engine

## ADDED Requirements

### Requirement: Transição de Estado baseada na Conexão do Jogador Humano
The system MUST detect login and logout events of the designated human player ("Tiago") and transition the AI Companion between `ACTIVE_COOP`, `ASSIGNED_MISSION`, and `AUTONOMOUS_SOLO` modes.

#### Scenario: Jogador humano loga no jogo
- **GIVEN** o companheiro IA está no modo `AUTONOMOUS_SOLO`
- **WHEN** o jogador humano "Tiago" efetua login no servidor
- **THEN** o sistema envia uma mensagem privada do bot cumprimentando o jogador humano e altera o estado para `ACTIVE_COOP`.

#### Scenario: Jogador humano envia missão antes de deslogar
- **GIVEN** o jogador humano está em diálogo de chat com o companheiro IA
- **WHEN** o jogador humano envia o comando "Farm Varnish em Abandoned Camp" e desloga
- **THEN** o bot salva a missão na tabela `companion_active_missions` e transita para o modo `ASSIGNED_MISSION`.

### Requirement: Progressão Autônoma de Nível 1 ao 40 e Mudança de Classe
The system MUST allow the AI Companion to farm PvE, sell loot directly to NPC merchants, and complete the 1st (Knight) and 2nd (Paladin) Class Transfer quests.

#### Scenario: Realização da 2nd Class Transfer no Nível 40
- **GIVEN** o bot atinge o Nível 40
- **WHEN** o bot possui os itens das provas de classe de Paladin
- **THEN** o bot interage com o Grand Master NPC e efetua a mudança de classe para Paladin.

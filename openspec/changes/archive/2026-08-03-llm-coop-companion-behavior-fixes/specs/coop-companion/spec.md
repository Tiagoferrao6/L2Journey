# Spec: Correção de Comportamentos Automáticos e Comandos Táticos

## Requirements

### Requirement: Respeito ao Dismiss de Party e Entrada sob Demanda
The system MUST NOT automatically add the AI Companion back into the human player's party when dismissed, and MUST only join the party upon explicit whisper request ("party") or direct party invite.

#### Scenario: Jogador remove o bot da party
- **GIVEN** o bot está na party em modo `ACTIVE_COOP`
- **WHEN** o jogador executa o Dismiss da party
- **THEN** o bot altera o estado para `AUTONOMOUS_SOLO` e permanece fora da party.

### Requirement: Acompanhamento Natural a Pé
The system MUST command the bot to follow the player using walking/running movement (`Intention.MOVE_TO`) for short/medium distances and MUST reserve teleportation for distances greater than 3000 units.

### Requirement: Comandos Táticos de Combate por PM
The system MUST support PM (whisper) tactical commands: `caca` (active hunting), `assist` (attack leader's target), `rest` (sit/recover), and `town` (teleport to Gludio town).

#### Scenario: Comando Assist em Combate
- **GIVEN** o jogador humano está focando o monstro "Keltir"
- **WHEN** o jogador envia a mensagem "assist" via PM para o bot
- **THEN** o bot altera seu alvo para "Keltir" e inicia o ataque.

#### Scenario: Comando Rest
- **WHEN** o jogador envia a mensagem "rest" via PM para o bot
- **THEN** o bot senta e entra em estado de recuperação de vida e mana.

#### Scenario: Comando Town
- **WHEN** o jogador envia a mensagem "town" via PM para o bot
- **THEN** o bot teleporta para a zona segura da vila de Gludio.

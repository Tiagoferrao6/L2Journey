# Spec: Trio Companheiro Co-op (Tanker, Arqueiro, Bishop)

## Requirements

### Requirement: Formação de Party Completa (Trio Co-op)
The system MUST spawn and join 3 distinct AI companion FakePlayers (`PaladinBot`, `HawkeyeBot`, `BishopBot`) into the player's Party upon request.

#### Scenario: Jogador inicia a jornada de Party do Nível 1
- **GIVEN** o jogador humano cria seu personagem Nível 1
- **WHEN** o jogador solicita a criação da party companheira (`party`)
- **THEN** o sistema gera os 3 companheiros no nível 1, os adiciona à party e os equipa automaticamente com armas e armaduras de grau No-Grade.

### Requirement: Papéis Táticos Especializados em Combate
The system MUST execute specialized AI roles for each companion member:
- `PaladinBot`: Tanker & Aggro control.
- `HawkeyeBot`: Ranged DPS assist.
- `BishopBot`: Emergency healing & MP management.

#### Scenario: Combate em grupo contra múltiplos monstros
- **WHEN** o líder humano ataca um monstro
- **THEN** o Tanker engaja o monstro, o Arqueiro dispara à distância no mesmo alvo, e o Bishop monitora e cura o HP do grupo.

### Requirement: Multi-Persona LLM no Chat
The system MUST respond to player whispers and party chat using distinct LLM Gamer personas for each companion.

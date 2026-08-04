# Spec: Painel HTML Interativo de Controle para Mercenários

## Requirements

### Requirement: Exibição do Painel HTML ao Interagir com Mercenário
The system MUST display an interactive HTML control dialog (`NpcHtmlMessage`) when a player targets and interacts with their active Mercenary/Companion.

#### Scenario: Jogador clica no Mercenário
- **GIVEN** o jogador possui um Mercenário ativo contratado
- **WHEN** o jogador clica para interagir com o Mercenário no jogo
- **THEN** o sistema exibe o painel HTML com status numérico de HP/MP e botões de ação rápidos.

### Requirement: Botões de Ação Dinâmicos via Bypass
The system MUST handle bypass actions (`mercenary_attack`, `mercenary_toggle_follow`, `mercenary_force_heal`, `mercenary_buff`, `mercenary_dismiss`) and update character state in real time.

#### Scenario: Clique no botão Atacar Alvo
- **GIVEN** o jogador humano selecionou o monstro "Keltir"
- **WHEN** o jogador clica no botão `[ ⚔️ ATACAR ALVO ]` no painel HTML
- **THEN** o mercenário foca o alvo "Keltir" e executa o ataque imediatamente.

### Requirement: Comandos de Voz `.merc` para Atalhos de Barra (F1-F12)
The system MUST support voiced commands (`.merc`, `.merc attack`, `.merc follow`, `.merc stay`, `.merc dismiss`) so players can assign mercenary commands to macro shortcut bars.

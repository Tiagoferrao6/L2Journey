# Proposal: Painel HTML Interativo de Controle para Mercenários e Companheiros

## Summary
Implementar uma janela HTML interativa (`NpcHtmlMessage`) exibida automaticamente ao selecionar e interagir com o Mercenário ou Companheiro (`PaladinBot` / `Elenora`), oferecendo botões de ação idênticos a um painel de controle (Atacar, Seguir/Ficar, Cura de Emergência, Buffs de Grupo, Parar e Dispensar), além do comando de voz `.merc`.

## Motivation
No cliente do Lineage 2, instâncias de `Player` / `FakePlayer` não ativam a aba nativa de Ações de Pet no menu `Alt + C`. Criar uma janela HTML interativa ao clicar no companheiro soluciona este problema e fornece uma interface rica, visual e com feedback em tempo real.

## Proposed Changes
- **HTML Dialog Handler (`ActionHandler` / `onAction`)**: Quando o jogador clica duas vezes ou interage com seu Mercenário/Companheiro, o servidor envia uma `NpcHtmlMessage` estilizada com o status atual e botões de comando.
- **Interactive Control Buttons**:
  - `[ ⚔️ ATACAR ALVO ]`: Executa `bypass -h mercenary_attack` (ataca o alvo focado pelo jogador).
  - `[ 🏃 SEGUIR / FICAR ]`: Executa `bypass -h mercenary_toggle_follow` (alterna entre seguir e ficar parado).
  - `[ 💊 CURAR AGORA ]`: Executa `bypass -h mercenary_force_heal` (força cura de emergência no líder).
  - `[ 🛡️ BUFFAR GRUPO ]`: Executa `bypass -h mercenary_buff` (renova buffs de suporte).
  - `[ 🚪 DISPENSAR ]`: Executa `bypass -h mercenary_dismiss` (desconecta o mercenário).
- **Voiced Command Handler (`VoicedCommandHandler`)**: Suporte a `.merc panel`, `.merc attack`, `.merc follow`, `.merc stay`, `.merc dismiss` para atalhos em macros (`F1-F12`).

## Verification
- Clicar no Mercenário no jogo e verificar a abertura do painel de controle HTML.
- Testar o clique nos botões de Ação (Atacar, Seguir, Sentar, Dispensar) e validar a execução em tempo real.
- Criar uma macro no jogo (`/merc attack`) e validar execução.

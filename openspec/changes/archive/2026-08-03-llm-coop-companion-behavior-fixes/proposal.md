# Proposal: Correção de Comportamentos Automáticos e Comandos Táticos do Agente Companheiro

## Summary
Ajustar as regras de comportamento do `LLMCompanionManager` para que o bot respeite a saída/expulsão de Party (Dismiss Party), utilize movimentação natural a pé (`Intention.MOVE_TO`) em distâncias curtas/médias, e passe a responder aos comandos táticos diretos por PM: `caca` (caça autônoma na área), `assist` (atacar o mesmo alvo do líder), `rest` (sentar para recuperar HP/MP) e `town` (retornar para a vila de Gludio).

## Motivation
Atualmente o bot re-adiciona a si próprio na Party a cada 3 segundos e possui poucos comandos interativos por mensagem privada. Adicionar comandos táticos diretos (`caca`, `assist`, `rest`, `town`) permite ao jogador humano comandar o companheiro como um verdadeiro parceiro de Party durante as caçadas.

## Proposed Changes
- **Respect Party Dismiss**: Quando o jogador sai ou remove o bot da Party, o estado do bot muda para `AUTONOMOUS_SOLO` sem forçar o auto-join.
- **Natural Walking & High-Distance Teleport**: O bot segue o líder a pé/correndo e só executa teletransporte se a distância for superior a 3000 unidades.
- **Tactical PM Commands**:
  - `caca` / `farm`: Bot inicia caça ativa de monstros visíveis na área.
  - `assist`: Bot ataca exatamente o alvo focado pelo jogador humano (`humanLeader.getTarget()`).
  - `rest` / `descansar`: Bot senta para regenerar HP/MP.
  - `town` / `cidade`: Bot teleporta para a zona segura da vila (Gludio GK).

## Verification
- Testar o Dismiss da Party e validar que o bot permanece fora do grupo.
- Testar o comando `assist` enquanto o jogador foca um mob.
- Testar os comandos `caca`, `rest` e `town` via PM.

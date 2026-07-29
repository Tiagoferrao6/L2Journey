# Proposal: Sistema de Mercenário Healer In-Game (Criação On-Demand, Persistência & Reset)

## Summary
Implementar a Prova de Conceito (MVP) do **Sistema de Mercenários In-Game (Mercenaries System)** voltado para a experiência Single Player do L2Journey, focado exclusivamente na contratação do **Mercenário Healer** (ex: *Elenora*):
- **Criação On-Demand & Nível do Jogador**: Ao clicar em **"Contratar Mercenário"** no `Alt + B`, o Healer é gerado na hora no **mesmo nível do personagem do jogador** (custo inicial: 1 Adena).
- **Persistência no MariaDB**: O estado, nível, EXP e configurações do Mercenário contratado são salvos no banco de dados vinculados ao personagem.
- **Botão de Reload / Reset de Contrato**: Permite ao jogador resincronizar o Mercenário com seu nível atual (preço: 1 Adena), resetando habilidades e equipando automaticamente o set da nova faixa de nível.
- **Configuração no `fakeplayers.ini`**: Ativação do sistema controlada pela chave `EnableMercenaries = True`.

## Motivation & Scope
- **Foco no MVP (Healer Only)**: Reduz o escopo inicial ao essencial para curas e suporte de party, garantindo rápida validação e estabilidade.
- **Progressão Sincronizada**: O Mercenário nasce no nível do jogador e ganha EXP junto com a party. O botão de Reload permite resincronizar a qualquer momento por 1 Adena.
- **Zero Roubo de Loot & Trade**: O mercenário não recolhe itens do chão (`canPickup = false`) e não abre janela de Trade.

## User Experience (UX)
1. **Contratação na Community Board (`Alt + B`)**: Botão **"Contratar Mercenário Healer (1 Adena)"**.
2. **Resincronização / Reset**: Botão **"Resetar Contrato / Reload (1 Adena)"** para trazer o Healer com a configuração inicial atualizada para o nível corrente do mestre.
3. **Persistência Transparente**: Ao relogar no jogo, seu Healer reaparece com o progresso salvo.

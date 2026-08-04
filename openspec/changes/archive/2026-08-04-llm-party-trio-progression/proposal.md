# Proposal: Trio Companheiro Co-op (Tanker, Arqueiro, Bishop) com Progressão Nível 1+

## Summary
Expandir a arquitetura do `LLMCompanionManager` para suportar uma Party completa de 3 bots companheiros autônomos (**PaladinBot** - Tanker, **HawkeyeBot** - Arqueiro, **BishopBot** - Curador) acompanhando o jogador humano desde o Nível 1. Cada bot terá papéis táticos distintos em combate e personas únicas com o LLM local (`qwen2.5:1.5b`).

## Motivation
Atualmente o sistema gerencia 1 companheiro individual ("PaladinBot"). Para testar o funcionamento completo da jogabilidade em grupo (Party Co-op) desde os níveis iniciais (Gremlins/Keltirs até Catacumbas), ter uma Party equilibrada (Tank, Dps Físico à Distância e Healer) permite validar a sinergia de combate, assistência de alvos, gerenciamento de mana/cura e interações dinâmicas por chat privado e no chat de Party.

## Proposed Changes
- **Multi-Companion Management (`LLMCompanionManager.java`)**: Suporte ao ciclo de vida e spawn conjunto dos 3 companheiros (`PaladinBot`, `HawkeyeBot`, `BishopBot`).
- **Tactical Combat Roles**:
  - **PaladinBot (Tanker)**: Puxa aggro, segura o mob no corpo a corpo e protege o jogador.
  - **HawkeyeBot (Arqueiro)**: Foca no alvo do líder humano (assist dps) mantendo distância de segurança.
  - **BishopBot (Curador)**: Executa curas de emergência (Battle Heal), suporte de buffs e recharge de mana.
- **Dynamic Multi-Persona LLM Prompting**: Prompts de sistema customizados para cada membro da Party responder PMs e conversas no chat de Party com suas respectivas personalidades (Tanker destemido, Arqueiro confiante, Bishop zeloso).

## Verification
- Digitar o comando `.merc party` ou PM `party` e validar o spawn dos 3 bots entrando no grupo do jogador.
- Acompanhar a evolução dos bots do Nível 1 ao 20 (1ª transferência de classe) e 40 (2ª transferência).
- Validar interações no chat de Party e respostas individuais via PM em Português BR com o Ollama (`qwen2.5:1.5b`).

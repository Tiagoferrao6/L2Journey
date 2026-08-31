## Context

Em Lineage 2, a arquitetura de rede acopla estritamente um `GameClient` (conexão de rede TCP) a um único objeto `Player`. Se uma mesma conta tenta logar novamente, a conexão anterior é derrubada ("Dual Login Kick"). Para viabilizar a "Família de Personagens", precisamos alterar a lógica de sessão e criar uma mecânica de "Ponte de Rede" que conecte pacotes a um dos `Player`s instanciados enquanto os outros operam com IA (FakePlayers).

## Goals / Non-Goals

**Goals:**
- Permitir que múltiplos `Player` objects vinculados a mesma `account_name` coexistam no mundo simulando jogadores diferentes.
- Implementar o "Fast Reload": a troca instantânea do alvo da rede do `GameClient` de `Player A` para `Player B` com reenvio de pacotes base (HUD, inventário).
- Converter instantaneamente o personagem desfocado em um FakePlayer autônomo (via `fakeplayer-ai-core`).

**Non-Goals:**
- A "Ilusão de Corpos" (Seamless Switch sem reload do cliente) fica fora do escopo inicial se causar Crash/Critical Error, preferindo o "Fast Reload".
- Modificar o cliente do jogo (modding de Client/System). Tudo deve ser resolvido via servidor.

## Decisions

**1. Container de Sessão de Família**
- *Decisão:* Adicionar uma lista `List<Player> _familyChars` no `GameClient` (ou em um `SessionManager`).
- *Rationale:* O GameClient precisa saber quais personagens no mundo pertencem àquela conexão de socket.

**2. Fluxo do "Fast Reload"**
- *Decisão:* Quando o comando `.switch` é chamado, o servidor executa:
  1. `PlayerA.setAI(new FakeHunterAI())` (vira bot)
  2. `GameClient.setActiveChar(PlayerB)`
  3. `PlayerB.setClient(GameClient)`
  4. Envia pacote `RestartResponse(1)` para dar clear na UI.
  5. Envia `CharacterSelected` e todo o login flow do PlayerB (UserInfo, Skills, Items).
- *Rationale:* O L2 Client quebra (crash) se o `ObjectId` principal mudar de repente sem os pacotes apropriados de reload. Isso emula um relog que dura milissegundos sem derrubar o TCP.

## Risks / Trade-offs

- **[Duplicação de Itens/Bugs]** Compartilhar o Warehouse ou fazer trades entre personagens da mesma conta online.
  - *Mitigação:* Bloquear trocas (Trade) e envio de correio (Mail) entre personagens instanciados pela mecânica de Família.
- **[Abuso de Farm/Multibox]** Um jogador controlando 9 personagens bots perfeitos.
  - *Mitigação:* Limitar a quantidade de personagens simultâneos (ex: máx de 2 ou 3) através de configs no `.ini`.

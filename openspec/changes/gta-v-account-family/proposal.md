## Why

O Lineage 2 tradicional força o jogador a passar por uma tela de login demorada para trocar de personagens (alts). Ao introduzir um sistema estilo "GTA V", queremos transformar a experiência de multicharacter, permitindo que personagens secundários da mesma conta (ex: um buffer ou crafter) fiquem no mundo de forma autônoma e o jogador possa "saltar" entre eles instantaneamente. Isso moderniza o gameplay, estimula a criação de múltiplos personagens e legaliza o "multibox" de forma balanceada.

## What Changes

- Implementação do conceito de "Account Family" no jogo, onde múltiplos `Player` objects da mesma conta podem estar instanciados no servidor.
- Criação de um comando/bypass `.switch <NomeDoAlt>` para trocar o controle ativo do `GameClient` de um personagem para outro.
- Adaptação do pacote `CharacterSelected` e do fluxo de entrada no jogo para permitir um "Fast Reload" (desconectar a interface atual e carregar a nova sem voltar ao lobby).
- Transição automática do personagem abandonado para o modo "OfflinePlay", injetando a Inteligência Artificial (do projeto `fakeplayer-ai-core`) nele.

## Capabilities

### New Capabilities
- `gta-v-seamless-switch`: Define as regras e limitações da troca rápida entre personagens da mesma conta.
- `account-family-session`: Define como o servidor gerencia a sessão de rede quando múltiplos personagens da mesma conta estão online.

### Modified Capabilities
- (Nenhuma)

## Impact

- `java/com/l2journey/gameserver/network/GameClient.java`: Modificação crítica para suportar a troca de `activeChar` sem fechar a conexão TCP.
- `java/com/l2journey/gameserver/model/actor/instance/Player.java`: Adaptações para transitar suavemente entre o estado controlado pelo humano e o estado controlado por AI.
- `java/com/l2journey/gameserver/network/serverpackets/`: Ajustes nos pacotes de inicialização (`UserInfo`, `ItemList`, etc) para garantir que o cliente limpe o estado anterior.

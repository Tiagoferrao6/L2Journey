## ADDED Requirements

### Requirement: Comando de Troca Rápida
O servidor DEVE prover um comando (ex: `.switch <Nome>`) que permita ao jogador transferir seu controle ativo para outro personagem da mesma conta que já esteja instanciado no mundo (no modo de Família).

#### Scenario: Troca Bem Sucedida
- **WHEN** o jogador controla a "SilverTester" e digita `.switch TitanTester`
- **THEN** a SilverTester é convertida para modo autônomo e o jogador assume o controle total do TitanTester instantaneamente

### Requirement: Reload de Interface Cliente
Ao realizar o Switch, o servidor DEVE reenviar os pacotes essenciais (`CharacterSelected`, `UserInfo`, `ItemList`, `SkillList`) sem desconectar o TCP, forçando o cliente do jogo a atualizar toda a HUD e modelo visual.

#### Scenario: Atualização de Skills
- **WHEN** a troca de personagens ocorre
- **THEN** a barra de atalhos e a lista de skills do cliente devem refletir apenas as habilidades do novo personagem focado

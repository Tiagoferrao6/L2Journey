## ADDED Requirements

### Requirement: Evocação da Família
O jogador DEVE poder invocar personagens secundários da sua conta para o mundo, desde que respeite o limite máximo de personagens ativos simultâneos definido nas configurações do servidor.

#### Scenario: Chamando um Alt
- **WHEN** o jogador utiliza o painel de Família para "Acordar" o TitanTester
- **THEN** o TitanTester é instanciado no mundo ao lado do jogador, operando sob a IA de FakePlayer

### Requirement: Persistência de Múltiplos Logins
O servidor NÃO DEVE desconectar automaticamente a conexão TCP quando um personagem secundário da mesma conta for inserido no mundo. A validação de "Conta Já Logada" no LoginServer/GameServer deve abrir exceção para personagens invocados pela mecânica de Família.

#### Scenario: Validação de Segurança
- **WHEN** o TitanTester é instanciado através da mecânica de Família
- **THEN** a sessão do GameClient permanece ativa e vinculada primordialmente à SilverTester (o personagem foco atual)

## 1. Sessão de Conta e Proteção de Login

- [ ] 1.1 Alterar a lógica do LoginServer/GameServer de "Account is already logged in" para permitir bypass se configurado
- [ ] 1.2 Adicionar rastreador de `FamilyCharacters` no `GameClient`
- [ ] 1.3 Bloquear operações de Trade e Mail entre personagens vinculados na mesma `Family`

## 2. Invocação do Secundário (Alt)

- [ ] 2.1 Criar comando `.call_alt <Nome>`
- [ ] 2.2 Ao invocar, carregar o `Player` object do banco de dados e dar spawn ao lado do jogador
- [ ] 2.3 Atribuir `FakeHunterAI` ao Alt recém-carregado
- [ ] 2.4 Salvar o Alt como um "FakePlayer offline" se o `GameClient` original for encerrado

## 3. O Mecanismo de Troca Rápida (.switch)

- [ ] 3.1 Criar comando `.switch <Nome>`
- [ ] 3.2 Desconectar lógicamente o `GameClient` do `Player` atual e transformar o `Player` atual em IA (bot)
- [ ] 3.3 Conectar o `GameClient` ao novo `Player` (o Alt)
- [ ] 3.4 Enviar os pacotes de `RestartResponse` (hack de interface) e em seguida os de Login (`UserInfo`, `ItemList`, `SkillList`)

## 4. Testes e Estabilidade

- [ ] 4.1 Invocar a SilverTester estando no controle do TitanTester
- [ ] 4.2 Usar o comando `.switch` e verificar se o cliente crasha ou se carrega os itens do outro personagem com sucesso
- [ ] 4.3 Testar deslogar da conta inteira para ver se os alts saem do mundo de forma limpa

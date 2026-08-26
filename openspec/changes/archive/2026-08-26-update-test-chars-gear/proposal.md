# Atualizar Setup e Equipamentos dos Personagens de Teste

## Context
Os personagens de teste `SilverTester` e `TitanTester` atualmente possuem equipamentos variados e skills incompletas no banco de dados (`z_custom_test_characters_setup.sql`). Para facilitar os testes de fim de jogo (endgame), mecânicas customizadas, raids (Baium, Valakas, Antharas) e testes de combate, é necessário que eles tenham inventários completos e otimizados, todas as skills (incluindo subclasses e encantos) e todos os itens customizados (Royal e Tattoos) no nível máximo.

## Scope
### In Scope
- Atualizar `z_custom_test_characters_setup.sql`.
- **Skills**: Garantir todas as skills aprendidas e encantadas, incluindo certificações de subclasse, e subclasses cumulativas (Moonlight Sentinel/Sword Muse para SilverTester; Titan/DreadNought para TitanTester).
- **Inventário de Suprimentos**: Adicionar consumíveis (Soul Ore, Spirit Ore, S-Grade Crystals, Battle Symbol) e itens de quest para entrar no Baium, Valakas e Antharas.
- **Equipamentos e Armas**: Providenciar um set Royal customizado (+6 full elemento), Joias Boss (+6) e todas as armas Royal (+6 full elemento) para ambos.
- **Tattoos**: Adicionar uma tattoo de cada tipo no nível máximo (Level 6) no inventário de cada personagem.

### Out of Scope
- Adicionar ou criar novos itens customizados na engine.
- Alterar as tabelas de itens e stats do servidor.

## Use Cases
- **Teste de Endgame**: QA e testadores poderão logar imediatamente nos personagens com equipamentos absolutos para testar instâncias, balanceamento e danos extremos.
- **Teste de Instâncias e Bosses**: Os itens de acesso estarão garantidos, evitando a necessidade de gerar itens via comandos de GM toda hora.

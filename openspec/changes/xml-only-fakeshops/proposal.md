## Why

Atualmente, o `FakePlayerManager` possui uma lógica acoplada que, se a tabela de banco de dados `fake_players_profiles` estiver vazia, ele injeta automaticamente 60 bots genéricos (30 Traders e 30 Hunters) para a cidade de Gludio. Isso entra em conflito com o design de lojas falsas (FakeShops) estáticas (ex: Gimli, Thorin, etc.) que são geridas puramente via XML (`fake_shops.xml`). Para garantir flexibilidade no futuro e desacoplar os FakeShops estáticos dos FakeHunters dinâmicos (que usarão IA e máquina de estados), precisamos forçar que os FakeShops sejam configurados **apenas via XML**.

## What Changes

- Remoção do método `initGludioProfilesIfEmpty()` do `FakePlayerManager` para interromper o flood de bots no banco de dados.
- O `FakePlayerManager` passará a aceitar **apenas** bots do tipo `HUNTER` se vierem da tabela `fake_players_profiles`.
- Qualquer loja, mercado ou NPC falso estático (Trader/Crafter) deve ser lido e instanciado exclusivamente pelo `FakeShopData` através do `fake_shops.xml`.
- A arquitetura dividirá perfeitamente as responsabilidades: Lojas = XML; Caçadores Ativos = SQL/IA.

## Capabilities

### New Capabilities
- `xml-only-fakeshops`: Limita a criação de bots da classe Trader e Shop ao sistema legado/XML para facilitar edições pontuais de inventário e posições sem impactar os bots dinâmicos gerenciados por banco de dados.

### Modified Capabilities
- (Nenhuma modificação nos requisitos de Spec já existentes).

## Impact

- `FakePlayerManager.java` será refatorado para não poluir o banco e ignorar Traders vindos do SQL.
- Necessário limpar o banco de dados atual para remover os 60 bots injetados indevidamente (SQL de limpeza opcional).
- Todos os FakeShops do jogo serão gerenciados exclusivamente em `data/fakeplayers/fake_shops.xml`.

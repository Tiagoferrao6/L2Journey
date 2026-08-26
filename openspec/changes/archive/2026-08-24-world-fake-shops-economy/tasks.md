## 1. Estruturação dos Catálogos por Cidade (`city_catalogs.xml`)

- [x] 1.1 Configurar os catálogos de Gludio (15 lojas) e Dion (15 lojas) com equipamentos e suprimentos de transição C e D-Grade
- [x] 1.2 Configurar o catálogo de Giran expandido para suprir 50 lojas com itens C, B, A e S-Grade, incluindo armas/sets encantados (+4/+8), refinados e oficinas
- [x] 1.3 Configurar o catálogo de Aden para 20 lojas exclusivamente com itens S80 e S84 encantados (+4/+8), Attribute Stones/Crystals (Elemento), Blessed Enchants S e Top Life Stones 80/84
- [x] 1.4 Configurar o catálogo das cidades secundárias (Oren, Hunters, Heine, Goddard, Rune, Schuttgart) restrito a 5 lojas de suprimentos e materiais primários cada

## 2. Configuração dos Perfis dos FakeShops (`fake_shops.xml`)

- [x] 2.1 Cadastrar 15 lojas em Gludio e 15 lojas em Dion focadas em C e D-Grade
- [x] 2.2 Cadastrar as 50 lojas de Giran com distribuição de SELL, BUY e CRAFT (C a S Grade)
- [x] 2.3 Cadastrar as 20 lojas de elite de Aden (Armas e Sets S80/S84 +4/+8, Elementos, Enchants Blessed S, Life Stones)
- [x] 2.4 Cadastrar 5 lojas de suprimentos para cada cidade secundária (Oren, Hunters, Heine, Goddard, Rune, Schuttgart)

## 3. Banco de Dados e Contas Persistentes

- [x] 3.1 Criar o script SQL `dist/db_installer/sql/game/world_fake_shops_accounts.sql` cadastrando todas as contas e personagens em lote para o ecossistema completo do mundo
- [x] 3.2 Executar o script SQL no banco `l2journey` garantindo a persistência das contas e a validação de login único no servidor

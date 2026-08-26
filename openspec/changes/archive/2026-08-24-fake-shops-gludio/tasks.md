## 1. Configuração e Fuso Horário

- [x] 1.1 Adicionar propriedade `FakePlayerAlwaysActive` em `Config.java` e `dist/game/config/npcs/fakeplayers.ini`
- [x] 1.2 Adicionar propriedades `FakePlayerShiftStartHour` e `FakePlayerShiftEndHour` em `Config.java` e `fakeplayers.ini`
- [x] 1.3 Garantir a checagem de horários sincronizada com o fuso horário de Brasília (`America/Sao_Paulo` / UTC-3)

## 2. Banco de Dados e Contas Persistentes

- [x] 2.1 Criar script SQL `dist/db_installer/sql/game/fake_shops_accounts.sql` para cadastrar as 15 contas e personagens fixos
- [x] 2.2 Garantir controle de sessão e trava de login único em `FakeShop` / `FakePlayerDAO` para evitar múltiplas instâncias por conta

## 3. Catálogo Top D-Grade e Perfil dos 15 FakeShops

- [x] 3.1 Atualizar `dist/game/data/fakeplayers/city_catalogs.xml` refinando as categorias para conter exclusivamente itens Top D-Grade (Brigandine Set, Manticore Set, Knowledge Robe Set, Elven Jewelry, Elven Long Sword, Mithril Dagger, Staff of Life, Bone Arrows, Enchants D)
- [x] 3.2 Atualizar `dist/game/data/fakeplayers/fake_shops.xml` com os 15 perfis fixos de Gludio (`Gimli`, `Thorin`, `Durin`, `Balin`, `Dwalin`, `Fili`, `Kili`, `Oin`, `Gloin`, `Bifur`, `Bofur`, `Bombur`, `Nori`, `Ori`, `Dori`) focados em itens Top D
- [x] 3.3 Configurar coordenadas X, Y, Z, Heading de cada loja com dispersão visual organizada na praça de Gludio

## 4. Gerenciamento e Ciclo de Vida (`FakePlayerManager`)

- [x] 4.1 Modificar `FakePlayerManager.java` para dar bypass na checagem de presença de players reais se `FakePlayerAlwaysActive = True`
- [x] 4.2 Ajustar o agendador de turnos para validar a janela configurada de início e fim baseada no relógio do servidor
- [x] 4.3 Testar inicialização e spawn dos 15 FakeShops no GameServer e verificar a montagem das lojas SELL, BUY e CRAFT

## Why

Atualmente o sistema de Fake Players ativa e desativa bots dinamicamente apenas quando há jogadores reais na zona de Gludio e utiliza instâncias em memória desvinculadas da tabela de contas (`accounts`) do MySQL. Para fins de testes completos e criação de um ambiente com economia viva e alta qualidade de equipamentos, precisamos de 15 FakeShops fixos em Gludio que funcionem 24/7, com contas registradas no banco de dados, verificação de login único, horário alinhado ao fuso de Brasília e um catálogo de vendas focado exclusivamente em **itens Top D-Grade** (melhores equipamentos, armas, armaduras, consumíveis e receitas D-Grade).

## What Changes

- **Sempre Ativos (24/7 para Testes)**: Adicionada opção em `fakeplayers.ini` (`FakePlayerAlwaysActive = True`) para permitir que os FakeShops fiquem online ininterruptamente sem depender da presença de players reais na zona.
- **Janela de Turno por Horário**: Adicionadas configurações de horário de início e fim (`FakePlayerShiftStartHour` e `FakePlayerShiftEndHour`) baseadas no relógio do sistema no fuso horário de Brasília (UTC-3).
- **15 FakeShops Fixos em Gludio**: Cadastro fixo de 15 bots comerciantes com nomes únicos, cada um com conta real registrada no banco de dados (`accounts` e `characters`) com trava contra múltiplos logins simultâneos da mesma conta.
- **Venda Exclusiva de Itens Top D-Grade**:
  - Reestruturação das categorias em `city_catalogs.xml` e `fake_shops.xml` garantindo que todos os equipamentos (armas, armaduras, joias), recipientes, consumíveis, flechas, shots e receitas à venda sejam exclusivamente **Top D-Grade** (ex: Brigandine Set, Manticore Set, Knowledge Robe Set, Elven Jewelry, Elven Long Sword, Mithril Dagger, Staff of Life, Bone Arrows, Enchants D, etc.).
  - Manutenção das modalidades SELL (Venda de Top D), BUY (Compra de matérias-primas por preços de mercado) e CRAFT (Oficinas de anões).
- **Persistência de Sessão e Posição**: Cada FakeShop efetua login como um jogador real, posiciona-se em coordenadas fixas no mercado de Gludio, monta a loja e permanece sentado até seu ciclo de renovação econômica.

## Capabilities

### New Capabilities
- `fake-shops-gludio`: Sistema de 15 FakeShops fixos para a cidade de Gludio com operação contínua 24/7, contas autenticadas no banco de dados, controle de login único e catálogo expandido com foco exclusivo em itens Top D-Grade.

### Modified Capabilities
- Nenhuma capacidade existente teve seus requisitos quebrados de forma incompatível.

## Impact

- `com.l2journey.Config`: Novas propriedades para controle de turnos por horário e modo Always Active.
- `com.l2journey.gameserver.managers.FakePlayerManager`: Refatoração da lógica de verificação de zona e agendamento para suporte ao modo contínuo e sincronia com fuso de Brasília.
- `com.l2journey.gameserver.data.xml.FakeShopData`: Leitura dos 15 perfis fixos de FakeShops e catálogos Top D-Grade.
- `dist/game/config/npcs/fakeplayers.ini`: Novos parâmetros de configuração.
- `dist/game/data/fakeplayers/fake_shops.xml` e `city_catalogs.xml`: Atualização e expansão dos XMLs com catálogo Top D-Grade.
- Banco de dados (`accounts`, `characters`, `fake_players_profiles`): Inserção das 15 contas e personagens fixos.

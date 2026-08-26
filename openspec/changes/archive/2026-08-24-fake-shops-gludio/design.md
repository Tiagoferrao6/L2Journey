## Context

O L2Journey possui uma estrutura inicial de Fake Players e FakeShops em `FakePlayerManager` e `FakeShopData`. Contudo, o modelo anterior dependia do gatilho de presença de jogadores reais na zona e criava instâncias de objetos `Player` em memória desvinculadas da tabela de contas (`accounts`) do MySQL.

Para habilitar testes contínuos e um mercado vivo de altíssima qualidade, este design formaliza a arquitetura dos **15 FakeShops Fixos em Gludio**, ativos 24/7 (ou sob turno com horários customizáveis no fuso de Brasília), autenticados no banco de dados, operando como jogadores reais e vendendo **exclusivamente itens Top D-Grade**.

## Goals / Non-Goals

**Goals:**
- **Atividade Contínua 24/7**: Permitir que os 15 FakeShops fiquem online ininterruptamente durante o período de testes.
- **Configuração de Turnos**: Suporte a parâmetros `FakePlayerShiftStartHour` (ex: `08:00`) e `FakePlayerShiftEndHour` (ex: `22:00`) sincronizados com o horário oficial de Brasília (`America/Sao_Paulo`).
- **Contas Persistentes e Únicas**: Cada bot possui conta cadastrada nas tabelas `accounts` e `characters`, com validação de login único na `L2GameClient` / `World`.
- **15 Lojas Fixas em Gludio**: Distribuição estratégica no mercado de Gludio cobrindo SELL (Venda), BUY (Compra) e CRAFT (Oficina de Anões).
- **Catálogo Focado em Top D-Grade**: Garantir que todas as armas, armaduras, joias, shots, enchants, suprimentos e oficinas ofereçam unicamente itens **Top D-Grade**.

**Non-Goals:**
- Venda de itens de graus inferiores (No-Grade ou D-grade básico/mid) nos FakeShops de Gludio.
- Expansão dos FakeShops para outras capitais nesta etapa.

## Decisions

### 1. Inserção SQL de Contas e Personagens Fixos
- **Decisão**: Criar o script SQL `dist/db_installer/sql/game/fake_shops_accounts.sql` para cadastrar as 15 contas e 15 personagens com IDs e nomes fixos.

### 2. Tabela de Distribuição dos 15 FakeShops Top D-Grade em Gludio

| # | Nome Bot | Tipo | Catálogo Top D-Grade Exclusivo | Classe | Coordenadas (X, Y, Z, Heading) |
|---|---|---|---|---|---|
| 1 | `Gimli` | SELL | Top D Materials (Steel, Cokes, Syn. Cokes, High Grade Suede, Coarse Bone Powder) | Bounty Hunter (53) | `-14228, 123445, -3115, 16384` |
| 2 | `Thorin` | SELL | Top D Refined Mats (Crafted Leather, Cord, Silver Mold, Braided Hemp) | Warsmith (56) | `-14180, 123480, -3115, 32768` |
| 3 | `Durin` | SELL | Top D Metals & Ores (Oriharukon Ore, Mithril Ore, Adamantite Nugget, Silver Nugget) | Bounty Hunter (53) | `-14130, 123520, -3115, 49152` |
| 4 | `Balin` | SELL | Enchant Weapon D, Enchant Armor D, Crystal D, Gemstone D | Scavenger (54) | `-14260, 123550, -3115, 0` |
| 5 | `Dwalin` | SELL | Soulshot D, Spiritshot D, Blessed Spiritshot D | Artisan (55) | `-14310, 123500, -3115, 16384` |
| 6 | `Fili` | SELL | Greater Healing Potion, Quick Healing Potion, Haste Potion, Scroll of Escape/Resurrection | Human Knight (9) | `-14350, 123445, -3115, 32768` |
| 7 | `Kili` | SELL | Top D Bows (Elven Bow, Crossbow of Night) & Bone Arrows | Elven Scout (22) | `-14228, 123380, -3115, 49152` |
| 8 | `Oin` | SELL | Top D Magic Weapons & Robes (Staff of Life, Atuba Mace, Knowledge Set) | Human Wizard (11) | `-14180, 123340, -3115, 0` |
| 9 | `Gloin` | SELL | Top D Heavy Armor & Melee Weapons (Brigandine Set, Elven Long Sword, Claymore, Glaive) | Orc Raider (45) | `-14130, 123380, -3115, 16384` |
| 10 | `Bifur` | SELL | Top D Light Armor & Daggers/Fists (Manticore Set, Mithril Dagger, Scallop Jamadhr) | Dark Assassin (32) | `-14260, 123340, -3115, 32768` |
| 11 | `Bofur` | BUY | Comprador de Insumos Top D (Iron Ore, Animal Bone, Charcoal, Coal, Varnish) | Scavenger (54) | `-14080, 123445, -3115, 49152` |
| 12 | `Bombur` | BUY | Comprador de Mats Raros & Key Parts Top D (Oriharukon Ore, Silver Nugget) | Scavenger (54) | `-14080, 123500, -3115, 0` |
| 13 | `Nori` | CRAFT | Oficina Dwarven Craft: Soulshots D & Blessed Spiritshots D | Warsmith (56) | `-14350, 123550, -3115, 16384` |
| 14 | `Ori` | CRAFT | Oficina Dwarven Craft: Steel, Cokes, Leather, Cord | Warsmith (56) | `-14380, 123500, -3115, 32768` |
| 15 | `Dori` | CRAFT | Oficina Dwarven Craft: Brigandine Set, Elven Long Sword, Mithril Dagger, Staff of Life | Warsmith (56) | `-14380, 123445, -3115, 49152` |

### 3. Integração com Fuso Horário de Brasília
- **Decisão**: Utilizar `LocalTime.now(ZoneId.of("America/Sao_Paulo"))` e `Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"))` nas checagens de turno.
- **Horário Padrão dos Turnos**: Se `FakePlayerAlwaysActive = False`, os horários padrão serão `FakePlayerShiftStartHour = "08:00"` e `FakePlayerShiftEndHour = "23:59"`.

## Risks / Trade-offs

- **[Risco] Impacto nos preços de mercado pelo foco em Top D** → *Mitigação*: Margens de minPrice e maxPrice calibradas em `city_catalogs.xml` para refletir o valor de itens Top D-Grade.
- **[Risco] Superlotação visual da praça de Gludio** → *Mitigação*: Coordenadas X/Y/Z ajustadas rigorosamente em raio de dispersão para evitar sobreposição de modelos 3D dos personagens.

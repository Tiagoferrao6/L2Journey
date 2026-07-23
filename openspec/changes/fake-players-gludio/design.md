## Context

Após definirmos a arquitetura técnica base de Lojas (`fake-traders-engine`) e de Combate (`epic-fake-players-dna`), o projeto precisa de uma implementação prática que sirva como "Piloto" para validar se os sistemas suportam a carga e se comportam conforme esperado na economia e no PvE. A região de Gludio foi escolhida por ser uma cidade low-level central.

## Architecture

Como este é um pacote de Conteúdo (Content Patch), não há novas classes Java ou alterações no Core. A arquitetura resume-se ao mapeamento geográfico e à calibração dos arquivos XML.

- **Mapeamento de Gludio (Traders)**:
  - 10 Bots SELL no Gludio Town Square.
  - 10 Bots BUY espalhados pelas pontes e saídas.
  - 10 Bots CRAFT (Soulshots, Weapons D-Grade) perto do Blacksmith e do Warehouse.
- **Mapeamento de Gludio (Hunters)**:
  - 10 Bots Solo em Ruins of Despair (Hunters com DNA furtivo/solitário).
  - 2 Parties (20 bots) cruzando entre Ruins of Agony e Gludio Gates.
- **Listeners de Zona (Otimização)**: Configurar no `server.ini` (ou hardcoded nos Managers) o raio de ativação de Gludio Town e das Ruínas para ativar o `Sleep Mode` da IA quando a área estiver vazia de players humanos.

## Decisions

- **Economia Focada**: Os arquivos XML deste piloto cobrirão apenas itens NG (No-Grade) e D-Grade, bem como materiais básicos. Isso evita quebrar a economia de high-level enquanto testamos o sumidouro de adena (Adena Sink).
- **Sem Intervenção no Core**: Toda essa implementação deve se limitar à criação e edição de `fake_traders_spawns.xml`, `fake_traders_economy.xml`, `fake_hunters_spawns.xml`, e `fake_hunters_dna.xml`.

## Risks / Trade-offs

- **Risco: Hunters atraírem Mobs para a cidade de Gludio (Train).**
  - *Mitigação:* Configurar os Hunters para soltarem os mobs (Wipe Aggro) e teleportarem (SoE) automaticamente se a distância para a cidade ficar abaixo de certo raio em relação à zona de perigo.
- **Risco: Traders ocuparem todos os espaços visíveis da cidade.**
  - *Mitigação:* Usar os raios de dispersão no `<location radius="X">` do XML para evitar aglomerações e clipping de texturas.

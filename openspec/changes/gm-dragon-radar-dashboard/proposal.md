# Proposal: GM Dragon Radar Dashboard

## Why

O Administrador/GM necessita de uma interface tática em tempo de execução para visualizar em tempo real a posição geográfica e o contexto dos companheiros de IA autônomos e entidades ao redor no servidor, inspirada no design retrô "Dragon Radar". Atualmente, a localização dos bots exige comandos de consulta textuais no console/API REST, o que dificulta o acompanhamento visual do comportamento de caça e kiting dos bots.

## What Changes

- **Novo Endpoint REST de Radar (`/api/admin/radar`)**: Fornece os dados espaciais normalizados $(X, Y, Z)$ do bot selecionado e de todas as criaturas/jogadores/mobs num raio de varredura parametrizável.
- **Interface Dragon Radar no Painel Web GM**:
  - Renderização gráfica em HTML5 Canvas circular com estética retrô verde CRT (glow & grid).
  - Posição centralizada no Bot ativo (triângulo vermelho fluorescente com indicação de orientação/heading).
  - Entidades próximas mapeadas por cores: Mobs (Amarelo), Mobs Agressivos (Roxo), Jogadores (Azul), Membros do Trio (Dourado).
  - 3 Níveis de Zoom selecionáveis: **500** (Micro/Combate), **2500** (Tático/Treino) e **5000** (Macro/Regional).
- **Interatividade & Controles de GM**:
  - Teletransporte rápido do GM para a localização do bot via clique no radar.
  - Alternância rápida entre os bots do trio (`PaladinBot`, `HawkeyeBot`, `BishopBot`).

## Capabilities

### New Capabilities
- `gm-dragon-radar`: Mapeamento visual 2D em tempo real e telemetria espacial dos bots de IA e entidades próximas no Painel GM.

### Modified Capabilities
- Nenhuma capacidade existente foi modificada nos requisitos spec.

## Impact

- **Backend**: `com.l2journey.gameserver.managers.WebAPIManager` adicionará o HTTP Context Handler `/api/admin/radar`.
- **Frontend**: Inclusão de componentes HTML5 Canvas e estilos CSS retrô na página de administração do servidor web (`data/html/admin/` ou REST Web Root).

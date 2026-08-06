# Design Document: bot-dynamic-hunting-zones

## Arquitetura do Sistema de Waypoints e Zonas

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                   FakePlayerHuntingZoneManager (Singleton)                  │
├─────────────────────────────────────────────────────────────────────────────┤
│  Carrega data/fakeplayer_hunting_zones.xml no boot do GameServer            │
│  - getZoneForLevel(int level): Retorna a zona de caça ideal                │
│  - getRandomWaypoint(HuntingZone zone): Seleciona um waypoint disponível   │
│  - getNextRoutePoint(HuntingWaypoint wp, int curStep): Retorna o próx. pto  │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                ┌──────────────────────┴──────────────────────┐
                │                                             │
                ▼                                             ▼
  ┌──────────────────────────┐                  ┌──────────────────────────┐
  │  ShirouTacticalEngine    │                  │  CrystalTacticalEngine   │
  │  (Tanker / Warlord AI)   │                  │  (Healer / Mage AI)      │
  └─────────────┬────────────┘                  └─────────────┬────────────┘
                │                                             │
                └──────────────────────┬──────────────────────┘
                                       │
                                       ▼
  ┌──────────────────────────────────────────────────────────────────────────┐
  │ 1. Checa se o bot excedeu o maxLevel da zona atual                       │
  │ 2. Se mudou de zona -> Teleporta/caminha para a nova zona de caça        │
  │ 3. Executa rota do Waypoint ativo (SINGLE / LINE / CIRCLE)               │
  │ 4. Se o waypoint está sem mobs ou sob KS -> Troca para outro Waypoint    │
  └──────────────────────────────────────────────────────────────────────────┘
```

## Estrutura do XML (`fakeplayer_hunting_zones.xml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<list>
    <!-- Nível 1 ao 15: Talking Island -->
    <zone id="TI_NORTH_FIELD" name="Talking Island North Field" minLevel="1" maxLevel="15">
        <waypoint id="wp_ti_1" type="SINGLE">
            <point x="-81000" y="245000" z="-3650" />
        </waypoint>
        <waypoint id="wp_ti_2" type="LINE">
            <point x="-80500" y="246000" z="-3640" />
            <point x="-79800" y="247200" z="-3600" />
            <point x="-79000" y="248500" z="-3550" />
        </waypoint>
        <waypoint id="wp_ti_3" type="CIRCLE">
            <point x="-82000" y="247000" z="-3650" />
            <point x="-81500" y="248000" z="-3620" />
            <point x="-80800" y="248200" z="-3600" />
            <point x="-81200" y="247200" z="-3630" />
        </waypoint>
    </zone>

    <!-- Nível 15 ao 25: Ruins of Despair / Gludio -->
    <zone id="RUINS_OF_DESPAIR" name="Ruins of Despair" minLevel="15" maxLevel="25">
        <waypoint id="wp_despair_1" type="SINGLE">
            <point x="-18450" y="145000" z="-3000" />
        </waypoint>
        <waypoint id="wp_despair_2" type="LINE">
            <point x="-17000" y="144000" z="-3000" />
            <point x="-16000" y="146000" z="-2950" />
        </waypoint>
    </zone>
</list>
```

## Modal Bot Inspector no Frontend GM (`web/index.html`)

O Modal será renderizado ao clicar no botão **🔍 Inspecionar** da tabela de FakePlayers:
- **Aba 1 (Geral & Equipamentos):** Exibe HP/MP/CP barras, Nível, Classe, Nome do Alvo Atual, e os 6 slots de Equipamento (Arma, Peito, Pernas, Capacete, Luvas, Botas).
- **Aba 2 (Inventário Completo):** Tabela de itens com Item ID, Nome, Quantidade e Indicador de Equipado.
- **Aba 3 (Buffs Ativos):** Tabela de buffs ativos com Skill ID, Nome, Nível e Duração Restante em segundos.

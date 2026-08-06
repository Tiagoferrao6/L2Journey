# Specification: Bot Dynamic Hunting & Waypoint Mesh

## Requirements

### Requirement: Hierarchical Hunting Zone Management
- O servidor MUST carregar o arquivo XML `data/fakeplayer_hunting_zones.xml`.
- O servidor MUST suportar até 20 waypoints por Zona de Caça.
- O servidor MUST associar cada zona a uma faixa de nível recomendada (`minLevel` e `maxLevel`).

### Requirement: Waypoint Types
- O servidor MUST suportar waypoints do tipo `SINGLE` (ponto único de patrulha local).
- O servidor MUST suportar waypoints do tipo `LINE` (patrulha de 2 a 5 pontos em ida e volta).
- O servidor MUST suportar waypoints do tipo `CIRCLE` (patrulha de 2 a 5 pontos em loop contínuo).

### Requirement: Dynamic Switching & Evasion
- O bot MUST trocar de waypoint dentro da mesma zona se o waypoint atual estiver sem mobs ou ocupado por outros players.
- O bot MUST transitar para uma nova Zona de Caça quando seu nível ultrapassar o `maxLevel` da zona atual.

### Requirement: Initial Consumables & Web Inspector
- Os bots companions MUST nascer com 2.000 Soulshots NG (auto-ativados) e 50 Poções de Cura.
- O Dashboard Web GM MUST exibir o modal **Bot Inspector** ao clicar em `🔍 Inspecionar`, mostrando inventário, equipamentos e buffs.

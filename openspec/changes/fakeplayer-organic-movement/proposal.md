## Why

A imersão é quebrada quando bots se teleportam magicamente ou ficam travados andando contra uma parede infinitamente devido a micro-erros de geodata. Para que os FakePlayers (e futuros bots de Família do modo GTA V) sejam indistinguíveis de jogadores reais, precisamos desenvolver uma fundação de "Movimentação Orgânica" (Organic Movement). Esse sistema forçará a IA a utilizar métodos nativos de interação com o servidor, respeitando a física, a economia e as restrições do jogo, e introduzirá o conceito de NavMesh Integrado com QGIS.

## What Changes

- Implementação de um mecanismo "Anti-Stuck" na IA base auxiliado por NavMesh.
- Importação e Exportação de rotas GeoJSON/KML integradas ao QGIS para desenhar Polígonos de Caça (Hunting Zones) e Linhas de Viagem (Safe Routes).
- Sistema de Matriz de Decisão (Custo vs Tempo): A IA calculará dinamicamente se vale a pena usar Gatekeeper (custo em Adena) ou se deslocar a pé (custo em tempo).
- Ensino de leitura de Bypasses HTML com Emulação de Clique Humano: A IA aguardará delays intencionais (ex: 1500ms) entre cliques na Gatekeeper para simular latência humana.
- Incorporação de uso de consumíveis (Scroll of Escape) e skills de teleporte (Party Return) com respeito ao tempo de cast.

## Capabilities

### New Capabilities
- `fakeplayer-organic-navigation`: Define a navegação por Polígonos e Linhas geradas via QGIS, além do mecanismo anti-stuck.
- `fakeplayer-immersive-teleport`: Define a matriz de decisão econômica (Adena vs Tempo) e o delay humano no uso de Gatekeepers e SOEs.

### Modified Capabilities
- (Nenhuma)

## Impact

- `java/com/l2journey/gameserver/model/actor/fakeplayer/*`: Adição da matriz de decisão econômica e suporte a Polígonos/Linhas.
- Custo de CPU: Algoritmos de roteamento e intersecção de polígonos consumirão processamento, requerendo cache eficiente.
- Ferramental Externo: Administradores usarão QGIS para desenhar o mundo, acelerando massivamente a criação de zonas de bots.

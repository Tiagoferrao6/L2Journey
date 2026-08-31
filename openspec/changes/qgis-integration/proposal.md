## Why

A criação e manipulação da malha de navegação (NavMesh) e Geodata manualmente é um processo doloroso e propenso a erros. Utilizar o QGIS (um software GIS profissional) como editor de níveis oficial para o L2Journey revoluciona o desenvolvimento. Precisamos de um pacote de scripts que não faça parte do core do jogo em si, mas que atue como uma ponte offline para extrair, converter e georreferenciar os dados do Lineage 2 para uso no QGIS.

## What Changes

- Criação de uma pasta raiz isolada chamada `qgis_tools/` (fora do path de build do Java).
- Criação de um manual passo-a-passo dentro dessa pasta (`QGIS_MANUAL.md`).
- Scripts em Python (ou utilitários standalone) para extração de Radar Maps.
- Script `minimap_stitcher.py` para costurar e georreferenciar os blocos de mapa (`SysTextures`) em uma única imagem gigante do mundo.
- Scripts para conversão de Geodata `.l2j` para Mapas de Elevação Raster (`.tif` / GeoTIFF).

## Capabilities

### New Capabilities
- `qgis-data-extractor`: Extrator e conversor offline de dados do Lineage 2 (Geodata e SysTextures) para formatos compatíveis com GIS (GeoTIFF e GeoJSON).

### Modified Capabilities
- (Nenhuma)

## Impact

- Zero impacto no tempo de build e performance do GameServer, já que todo o código residirá isolado na pasta `qgis_tools/`.
- Impacto massivo e positivo no fluxo de trabalho dos desenvolvedores, permitindo a criação visual de NavMeshes e Zonas de Caça.

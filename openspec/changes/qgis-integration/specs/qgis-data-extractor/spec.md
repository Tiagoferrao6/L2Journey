# Extrator de Dados e Conversor QGIS

## Core Requirements
1. O pacote de ferramentas deve residir no diretório `/qgis_tools/` na raiz do repositório (ao lado de `/java`, `/dist`, etc.).
2. Ele não pode estar acoplado ou adicionar dependências ao core do Java ou ao build do servidor.
3. Deve incluir os requerimentos de instalação (ex: `requirements.txt` se em Python).

## Geodata to GeoTIFF Converter
1. A ferramenta `l2j_to_tiff.py` deve ler arquivos padronizados `.l2j`.
2. A leitura da Geodata envolve decodificar o formato estruturado do Lineage 2 onde:
   - 1 Bloco de Geodata = 256x256 grids (65536 coordenadas).
   - Cada grid é mapeada com altura `Z` e flags NSWE (Norte, Sul, Oeste, Leste).
3. O conversor precisa gerar um raster GeoTIFF onde o valor do Pixel seja equivalente à altitude (eixo Z).
4. Essa camada de elevação será sobreposta no QGIS para o desenvolvedor conseguir enxergar os relevos (montanhas) e identificar visualmente os locais inalcançáveis da malha.

## Minimap Texture Stitcher
1. A ferramenta `minimap_stitcher.py` deve processar a pasta de texturas do Minimap extraídas do `SysTextures` (ex: arquivos `15_20.bmp`, `16_20.bmp`, etc).
2. Usando a biblioteca `Pillow` (PIL), deve ler os nomes matemáticos dos arquivos e inferir as posições corretas na grade global do L2.
3. Deve colar todos os fragmentos em uma única imagem mestre (Mosaico Global).
4. A imagem gerada será a camada visual base para o fundo do QGIS.

## QGIS Manual (`QGIS_MANUAL.md`)
1. Um documento didático com passo a passo.
2. Deve ensinar a:
   - Definir o CRS (Coordinate Reference System) Cartesiano.
   - Usar a ferramenta Georeferencer do QGIS para importar as texturas das cidades (Minimap) usando o Ponto Min/Max e fixá-las no grid 3D do L2.
   - Carregar o GeoTIFF de relevo criado pelo extrator e aplicar uma paleta Pseudocolor (Singleband).
   - Importar o `NavMesh.geojson` criado in-game.
   - Criar do zero um arquivo `Polygon` (`.geojson`) no QGIS para novas Zonas de Caça e salvar no `/data/navmesh/` do servidor.

## Context

Temos o nosso motor orgânico (NavMeshManager) operando dentro do L2Journey usando arquivos `.geojson`. No entanto, para criar esses arquivos com precisão, precisamos enxergar o mundo do Lineage 2 no QGIS. A Geodata nativa e os mapas do jogo não são diretamente legíveis por softwares GIS.

## Goals / Non-Goals

**Goals:**
- Criar um pacote standalone de ferramentas na raiz do projeto chamado `qgis_tools/`.
- Fornecer scripts Python limpos que convertam matrizes de Geodata L2J (arquivos `.l2j` padrão) para **GeoTIFFs** de elevação.
- Fornecer um script (`minimap_stitcher.py`) que usa a biblioteca Pillow (PIL) para ler as dezenas de pedaços de radar do L2 e costurá-los em um mapa global georreferenciável.
- Fornecer um utilitário que converte parâmetros de sistema de coordenadas e georreferencia imagens automaticamente.
- Escrever um manual markdown definitivo (`QGIS_MANUAL.md`) ensinando o administrador a instalar o QGIS, importar os mapas, desenhar os polígonos e exportar de volta para o servidor.

**Non-Goals:**
- Não iremos modificar o `build.xml` do Java, essas ferramentas são 100% utilitárias e separadas.
- Não iremos criar um Client Mod. Isso é puramente manipulação de dados Server-Side e mapas para Game Masters.

## Decisions

**1. Linguagem das Ferramentas Offline**
- *Decisão:* Os scripts conversores serão escritos em Python 3.
- *Rationale:* Python possui as melhores bibliotecas para manipulação GIS (como `gdal`, `rasterio` e `numpy`), tornando a extração da matriz 3D da geodata em arquivos GeoTIFF incrivelmente fácil.

**2. Sistema de Coordenadas de Referência (CRS)**
- *Decisão:* No QGIS, instruiremos os usuários a usar as Coordenadas Cartesianas base do Lineage 2 (onde 1 unidade = 1 unidade ingame) em vez de tentar forçar um mapa mundi pseudo-Mergator.
- *Rationale:* Facilita o plug-and-play do `GeoJSON`. Se você desenhar um ponto no X=15000 Y=140000 no QGIS, esse ponto será exato in-game, sem precisar de cálculos complexos de trigonometria e conversão.

## Risks / Trade-offs

- **[Tamanho do Arquivo]** A conversão da Geodata `.l2j` para TIFF de alta resolução pode gerar arquivos enormes (gigabytes) dependendo da granularidade que o Python extrair.
  - *Mitigação:* O script Python terá um parâmetro de _Downsampling_ para permitir criar mapas de elevação leves focados em visualizar regiões amplas, reduzindo drasticamente o tamanho do raster de elevação.

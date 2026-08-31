## 1. Infraestrutura Python Isolada
- [x] 1.1 Criar a pasta raiz `/qgis_tools/` no topo do repositório
- [x] 1.2 Criar um `requirements.txt` com as dependências `gdal` e `rasterio`

## 2. Geodata L2J Parser
- [x] 2.1 Criar `l2j_to_tiff.py` na pasta `qgis_tools/`
- [x] 2.2 Desenvolver a lógica de decodificação binária do `.l2j` para extrair os 65536 blocos de Z-Height e salvá-los no array raster
- [x] 2.3 Utilizar a biblioteca TIFF (rasterio) para aplicar as coordenadas corretas e compilar o mapa de elevação

## 3. Minimap Texture Stitcher
- [x] 3.1 Criar `minimap_stitcher.py` na pasta `qgis_tools/` e adicionar `Pillow` ao `requirements.txt`
- [x] 3.2 Programar a extração da matriz X e Y pelo nome do arquivo (ex: `20_20.bmp`)
- [x] 3.3 Colar as texturas ordenadas em um canvas gigante usando Pillow e exportar como `.png`

## 4. Manuais e Guias QGIS
- [x] 4.1 Criar o arquivo `QGIS_MANUAL.md` na pasta `qgis_tools/`
- [x] 4.2 Documentar detalhadamente o Georreferenciamento e o Sistema de Coordenadas (CRS)
- [x] 4.3 Adicionar passo a passo de importação e exportação do formato nativo (GeoJSON) que nosso motor Java compreende

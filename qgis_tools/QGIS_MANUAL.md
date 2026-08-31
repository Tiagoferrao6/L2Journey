# Manual de Integração QGIS x L2Journey

Este manual orienta administradores e desenvolvedores a configurarem o QGIS para criar rotas e zonas de caça visuais (NavMeshes) utilizando os dados extraídos do Lineage 2.

## Pré-requisitos
1. Instale o [QGIS](https://qgis.org/pt_BR/site/forusers/download.html).
2. Tenha instalado o Python 3.
3. Instale as dependências da pasta `qgis_tools`:
   ```bash
   pip install -r requirements.txt
   ```

## 1. Costurando o Mapa Global
As imagens de Minimap originais do Lineage 2 (extraídas do `SysTextures` via L2Tool ou UModel) vêm cortadas em dezenas de pequenos quadrados (ex: `20_20.bmp`).

1. Crie uma pasta chamada `tiles` dentro de `qgis_tools/`.
2. Jogue todos os arquivos `.bmp` do radar lá dentro.
3. Rode o script de mosaico:
   ```bash
   python minimap_stitcher.py
   ```
4. O script vai gerar um arquivo gigante chamado `L2_Global_Map.png`. Este é o nosso quadro em branco.

## 2. Configurando o Projeto no QGIS (Georreferenciamento)
O Lineage 2 usa coordenadas cartesianas X/Y puras, e não Latitude/Longitude.

1. Abra o QGIS e crie um **Novo Projeto**.
2. Vá em **Projeto > Propriedades > SRC**.
3. Escolha o SRC genérico `EPSG:3857` (Pseudo-Mercator) apenas para termos um plano métrico.
4. Vá em **Raster > Georreferenciador**.
5. Abra a imagem `L2_Global_Map.png`.
6. Adicione Pontos de Controle (GCPs):
   - No L2, a região `20_18` (Ponto superior esquerdo clássico de Aden/Giran) começa no X = `0` e Y = `0` (baseando-se no offset do mapa, ou calcule a borda exata `(RegionX - 20) * 32768`).
   - Use os cantos extremos do mapa para referenciar os pixels da imagem para a coordenada global in-game.
7. Clique no botão de Play verde (Iniciar Georreferenciamento) e o QGIS vai cuspir a imagem no canvas oficial.

## 3. Visualizando as Montanhas (Geodata para Elevação)
Para saber se os Bots conseguirão passar por um lugar, você precisa ver a elevação do terreno (Z).

1. Jogue seus arquivos `.l2j` do GameServer na mesma pasta do script de elevação.
2. (Você deve adaptar o script para fazer um for loop em todos os `.l2j` que deseja compilar).
3. Rode:
   ```bash
   python l2j_to_tiff.py
   ```
4. Ele gerará um arquivo `.tif` (ex: `20_20_elevation.tif`).
5. Arraste esse `.tif` para a aba "Camadas" do QGIS.
6. Clique com botão direito na camada -> **Propriedades > Simbologia**.
7. Mude de "Banda Simples Cinza" para "Banda Simples Falsa Cor" (Pseudocolor) e escolha um degradê (ex: Azul para Baixo, Vermelho para Alto). Agora você vê relevo!

## 4. Editando NavMesh e Waypoints (A Integração de Fato)
Toda a magia acontece usando arquivos `GeoJSON` (Lines e Polygons).

### Exportando do Jogo para o QGIS
1. Logue com o seu GM e digite `//admin_rec_path RotaDaGK`. Ande um pouco e digite `//admin_stop_rec`.
2. Pegue o arquivo `rota_giran.geojson` que foi gerado na pasta `data/navmesh/` do seu GameServer.
3. Arraste o arquivo para dentro do QGIS. A linha aparecerá perfeitamente pintada em cima da rua que você andou.

### Desenhando no QGIS para o Jogo
1. No QGIS, vá em **Camada > Criar Camada > Nova Camada GeoJSON**.
2. Tipo de Geometria: `Polígono`.
3. Adicione um campo na tabela de atributos (ex: `zone_id` = String).
4. Clique no Lápis Amarelo (Alternar Edição) e desenhe um polígono circulando a área de Farm do Dragon Valley.
5. Salve o arquivo GeoJSON na pasta `data/navmesh/` do servidor.
6. O `NavMeshManager` em Java carregará essa área no próximo boot, e o `FakeHunterAI` restringirá a perseguição de monstros para os limites do seu polígono desenhado à mão!

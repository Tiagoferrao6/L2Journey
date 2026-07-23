#!/bin/bash

# Nome do arquivo de saída
OUTPUT="conteudo_configuracoes.txt"

# Limpa o arquivo de saída caso ele já exista
> "$OUTPUT"

# Lista com os principais arquivos de configuração identificados
CONFS=(
    "dist/login/config/database.ini"
    "dist/login/config/loginserver.ini"
    "dist/game/config/database.ini"
    "dist/game/config/server.ini"
    "dist/game/config/general.ini"
    "dist/game/config/admin/rates.ini"
    "dist/game/config/admin/network.ini"
    "dist/game/config/player/startinglocation.ini"
)

echo "=== Iniciando agrupamento das configurações ==="

for FILE in "${CONFS[@]}"; do
    if [ -f "$FILE" ]; then
        echo "Adicionando: $FILE"
        # Adiciona uma borda visual para separar os arquivos no arquivo final
        echo "================================================================================" >> "$OUTPUT"
        echo " ARQUIVO: $FILE" >> "$OUTPUT"
        echo "================================================================================" >> "$OUTPUT"
        
        # Injeta o conteúdo do arquivo original
        cat "$FILE" >> "$OUTPUT"
        
        # Adiciona linhas em branco de espaçamento
        echo -e "\n\n" >> "$OUTPUT"
    else
        echo "[Aviso] Arquivo não encontrado: $FILE"
    fi
done

echo "=== Processo concluído! Arquivo gerado: $OUTPUT ==="

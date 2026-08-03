# Proposal: Painel Inspetor Detalhado de Fake Hunters no Dashboard de GM

## Summary
Expandir os endpoints do servidor REST HTTP embutido (`WebAPIManager.java`) e o Dashboard de GM (`web/index.html`) para permitir inspeção profunda e detalhada em tempo real dos bots FakeHunter e FakeTrader. O Administrador/GM poderá visualizar CP, inventário completo, armas e armaduras equipadas, lista de buffs ativos com duração, árvore de habilidades e histórico de uso de skills.

## Motivation
Atualmente o Dashboard de GM exibe estatísticas gerais de nível, tipo (Hunter/Trader), zona e porcentagem de HP/MP. Para monitoramento comportamental completo de bots autônomos e resolução de dúvidas de balanceamento em tempo real, o GM precisa inspecionar a bolsa, paperdoll e buffs de qualquer bot sem abrir a ferramenta de desenvolvedor do banco de dados.

## Proposed Changes
- **API Extension (`/api/admin/fakeplayers/{name}`)**: Novo endpoint REST detalhado no `WebAPIManager.java` que extrai e expõe o modelo completo do bot (HP/MP/CP absolutos, lista de itens do inventário, equipados no paperdoll, buffs ativos e skills).
- **Frontend Inspector Modal (`web/index.html`)**: Adicionar um modal interativo "Bot Inspector" que abre ao clicar na linha do bot na tabela do Dashboard de GM.

## Verification
- Chamar o endpoint `/api/admin/fakeplayers/DespairArcher` via `curl` ou Fetch e verificar se o JSON retorna a árvore de inventário, buffs e paperdoll.
- Abrir o dashboard web e testar o clique na tabela de FakePlayers para abrir o modal de inspeção.

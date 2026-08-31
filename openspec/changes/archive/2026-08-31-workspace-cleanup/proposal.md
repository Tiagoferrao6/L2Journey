# Workspace Cleanup & SQL Verification

## Objetivo
Mapear, verificar e organizar todos os arquivos de configuração, scripts auxiliares (`.py`), dados temporários (`.json`, `.txt`) e scripts SQL (`.sql`) utilizados no projeto L2Journey.
O foco é garantir que o projeto possa ser recompilado e montado do zero sem problemas, mantendo as configurações atuais. Arquivos obsoletos ou temporários serão limpos.

## Estado Atual e Análise
Ao longo das implementações e testes recentes, acumulamos vários arquivos na raiz do repositório:
- **Scripts Python**: `generate_sql.py`, `generate_full_sql.py`, `fix_client_dat.py`, `historixotxt.py`.
- **Arquivos Temporários/Dados**: `stat1.json` até `stat4.json`, `temp_out.txt`, `temp_weap.txt`, `silver_skills.txt`, `titan_skills.txt`.

### Banco de Dados (SQLs)
O inicializador do banco (`docker/mysql/init/00_run_sql.sh`) lê iterativamente todos os arquivos `.sql` válidos na pasta `dist/db_installer/sql/game` e `login`. Ele ignora os arquivos com sufixo `.disabled`.
- Isso significa que qualquer script colocado em `dist/db_installer/sql/game` será rodado automaticamente em uma nova montagem do banco.
- Scripts como `cleanup_old_fake_shops.sql` (que criamos antes) e `z_custom_test_characters_setup.sql` estão garantidos de serem rodados caso compilemos o servidor do zero, mantendo nossas configurações de fakeshops, SilverTester e TitanTester.

## Solução Proposta
1. **Organização de Ferramentas**: Criar uma pasta `/tools` (ou mover para um local como `/Extra/tools`) para guardar os scripts Python vitais que nos auxiliam (`fix_client_dat.py`, `generate_full_sql.py`, etc).
2. **Limpeza**: Deletar os arquivos de texto e jsons temporários que já cumpriram sua utilidade.
3. **Documentação de Instalação do DB**: Criar um arquivo `README.md` na pasta `dist/db_installer/sql` explicando a ordem de carregamento (que é alfabética) e como habilitar/desabilitar os `.sql.disabled`.

## Validação
O projeto deverá estar com a raiz limpa e sem "sujeiras". Uma nova montagem do docker compose deve funcionar carregando perfeitamente todas as configurações de testes e personagens (incluindo os fakeshops) a partir da execução do SQL.

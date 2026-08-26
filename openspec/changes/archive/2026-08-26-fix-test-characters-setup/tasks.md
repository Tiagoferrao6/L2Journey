## 1. Script SQL Unificado de Configuração dos Personagens de Teste

- [x] 1.1 Criar o script SQL `dist/db_installer/sql/game/custom_test_characters_setup.sql` agrupando o cadastro completo e limpo de `SilverTester` (300000000) e `TitanTester` (300000001)
- [x] 1.2 Incluir entradas explícitas de `class_index = 0` para `SilverTester` (Moonlight Sentinel - 102) e `TitanTester` (Titan - 113) em `character_subclasses`
- [x] 1.3 Calibrar quantidades de Soulshot S e Spiritshot S para 5.000 unidades cada, mantendo peso do inventário < 100%
- [x] 1.4 Registrar ambos os personagens na tabela `heroes` e provisionar `clan_data` e `clan_skills` para o clan `100000` (`TesterClan`)
- [x] 1.5 Ajustar coordenadas Z para `-3404` no solo da praça de Giran

## 2. Limpeza de Arquivos Antigos e Validação

- [x] 2.1 Remover o script antigo `dist/db_installer/sql/game/custom_titan_tester_setup.sql` em favor do script unificado `custom_test_characters_setup.sql`
- [x] 2.2 Executar o script SQL no banco `l2journey` e validar a persistência dos dados dos dois personagens de teste

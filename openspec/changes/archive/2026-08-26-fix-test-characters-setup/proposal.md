# Proposal: Fix SilverTester and TitanTester Test Character Setups

## Why

Os dois personagens de teste do servidor, `SilverTester` (`300000000`) e `TitanTester` (`300000001`), apresentavam bugs críticos ao logar no jogo:
1. **Aparência e Modelo 3D Corrompidos**: Ambos apareciam como o modelo padrão de substituição (Female Dwarf), pois faltava a entrada de registro da classe principal (`class_index = 0`) na tabela `character_subclasses`.
2. **Personagem Flutuando no Vazio Azul**: A ausência do carregamento da classe base combinado a pequenas variações da coordenada Z (`z = -3400` em vez de `-3404` no solo de Giran) impedia o vínculo com a malha do mapa.
3. **Trava de Movimentação por Peso (185.45% Overweight)**: As pilhas gigantes de 100.000 Soulshots S e 100.000 Spiritshots S geravam mais de 600.000 de peso no inventário (a capacidade do personagem é ~90.000).
4. **Falta de Script Unificado no Datapack**: `SilverTester` não possuía um script de instalação dedicado em `dist/db_installer/sql/game/`, fazendo com que reinstalações do banco o deixassem incompleto ou sem status de Hero/Clan.

## What Changes

- **Inclusão Obrigatória do `class_index = 0` (Base Class)**:
  - Garantir registro de `Moonlight Sentinel` (ID 102) para `SilverTester` e `Titan` (ID 113) para `TitanTester` com `class_index = 0` na tabela `character_subclasses`.
- **Calibragem de Peso do Inventário (0% Sobreposição)**:
  - Reduzir as pilhas de Soulshot S e Spiritshot S para 5.000 unidades por personagem, eliminando o limite de sobrecarga de peso e permitindo movimentação e combate normais.
- **Sincronização de Posição de Spawn**:
  - Definir `x = 83400`, `y = 147940`, `z = -3404` para ambos os personagens no solo exato da praça de Giran.
- **Hero Status e Clan Privileges Unificados**:
  - Registrar ambos os personagens na tabela `heroes` (`claimed = 'true'`, `played = 1`) com suas respectivas classe IDs (102 e 113) e garantir que pertencem ao `TesterClan` (`clanid = 100000`, Nível 11) com privilégios de liderança.
- **Script Unificado de Instalação no Datapack**:
  - Criar `dist/db_installer/sql/game/custom_test_characters_setup.sql` agrupando a criação idempotente e limpa dos dois personagens.

## Capabilities

### Modified Capabilities
- `test-character-setup`: Atualiza os requisitos de provisionamento dos personagens de teste `SilverTester` e `TitanTester` para garantir carregamento 3D correto da classe base, sem sobrecarga de peso e com status de Hero/Clan funcionais.

## Impact

- **Database**: Atualiza as tabelas `characters`, `character_subclasses`, `character_skills`, `items`, `heroes`, `clan_data` e `clan_skills` para os IDs `300000000` e `300000001`.
- **Datapack**: Substitui o arquivo SQL individual por `dist/db_installer/sql/game/custom_test_characters_setup.sql`.

# Tasks: Fix Bot Localization & 24/7 FakeHunters Active Mode

## 1. Localização e Segurança para FakePlayers
- [x] **1.1 Null Safety em `NpcNameLocalisationData.java`**
  - Adicionar checagem `if (lang == null) return null;` para evitar `NullPointerException` em `ConcurrentHashMap.get(null)`.
- [x] **1.2 Definir Idioma Padrão ("en") no `FakePlayer.java`**
  - Inicializar `setLang("en")` no construtor de `FakePlayer` para que mensagens de dano e buscas de localização recebam o código de idioma em inglês.

## 2. Operação 24/7 para FakeHunters
- [x] **2.1 Isenção de Despawn por Shift em `FakeHunterManager.java`**
  - Atualizar o `ShiftTick` para manter todos os FakeHunters ativos 24/7 em jogo.

## 3. Validação e Rebuild
- [x] **3.1 Build dos Containers & Teste em Jogo**
  - Compilar via Ant/Podman e verificar logs para confirmar ausência de NPE e permanência 24/7 dos bots.

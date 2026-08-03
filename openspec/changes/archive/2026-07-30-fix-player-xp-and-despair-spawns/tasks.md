# Tasks: Fix Player XP, Mercenary Companion Party UI & Despair Spawns

## 1. Correção do XP do Player & Sincronização do Companion
- [x] **1.1 Ajuste do Nível do Mercenário para o Nível do Mestre**
  - Sincronizar nível da `MercenaryInstance` para corresponder exatamente ao nível do jogador contratante.
- [x] **1.2 Isenção de Cutoff de Nível e Preservação de 100% XP para o Player**
  - Isentar o Mercenário do cálculo de `topLvl` em `Party.java` e atribuir 100% da experiência de combate ao jogador humano.

## 2. Interface de Party do Companion (Party UI) & Auto-Teleport
- [x] **2.1 Envio do Pacote `PartySmallWindowAll` no Spawn da Party**
  - Garantir inicialização do frame de Party no cliente Lineage II ao contratar o Mercenário.
- [x] **2.2 Garantia de Auto-Teleport do Companion**
  - Confirmar teleporte sincronizado do Mercenário acompanhando qualquer deslocamento do Mestre.

## 3. Configuração do Tester (Nível 20 + Top D-Grade)
- [x] **3.1 Ajuste do Personagem Tester para Nível 20**
  - Definir nível 20 e aplicar kit Top D-Grade completo (Set, Arma e Joias).

## 4. Spawns e Comportamento dos 5 Bots em Ruins of Despair
- [x] **4.1 Correção de Coordenadas no XML (`fake_hunters_spawns.xml`)**
  - Atualizar coordenadas para `X: -19120, Y: 136816, Z: -3752` (zona de caça real de Ruins of Despair).
- [x] **4.2 Ativação Permanente (Sem Sleep Mode)**
  - Configurar os 5 bots para permanecerem 100% ativos de forma independente.
- [x] **4.3 Implementação das 5 Classes & Comportamentos Específicos**
  - 1 Arqueiro Solo (Kiting & Shots)
  - Party de 3 (Tank com Aggro + Healer de Suporte + Dagger Ataque pelas Costas)
  - 1 Anão Spoil (Skill Spoil + Sweep no cadáver)

## 5. Build, Deploy & Validação E2E
- [x] **5.1 Rebuild dos Containers & Teste no Jogo**
  - Compilação via Ant/Podman e validação dos 5 bots em Ruins of Despair e da interface da party.

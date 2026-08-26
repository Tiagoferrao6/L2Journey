# Correção de Arquivos do Client (Dat) e Inclusão das Tattoos

## Context
Durante a exploração dos arquivos textuais do client (`client_dat/`), foi identificado que os itens customizados (Royal Weapons e Royal Armors) possuem espaçamento e estruturação de colunas **completamente inválidas**. 
Além disso, as Tattoos customizadas (IDs 41001 a 41024), que concedem bônus massivos no servidor, **não existem** no client, resultando em itens invisíveis ou com erro (quadrados pretos e "No Name") in-game.

### Problemas Encontrados na Verificação:
1. **Royal Weapons (`weapongrp.txt`)**: 
   A arma *Royal Dynasty Blade (99300)*, por exemplo, não carrega a aparência correta da Dynasty Blade. Quem a adicionou usou o formato de colunas do `armorgrp.txt` (iniciando com o valor `1` na primeira coluna e com as texturas de `drop_items` no meio), o que causa um conflito direto com a estrutura do `weapongrp.txt` (que deveria começar com `0` e definir os meshes `LineageWeapons.dynasty_blade_m00_wp` corretamente). **Isso resultará em um Critical Error ao abrir o inventário.**
2. **Royal Armors (`armorgrp.txt`)**:
   As armaduras estão com um "padding" forçado (dezenas de colunas preenchidas com zeros cegamente no final da linha). No client High Five, o `armorgrp` exige que todas as raças (Human, Elf, Dark Elf, Orc, Dwarf, Kamael) tenham seus meshes (`LineageArmor.t91_u_m00`) e texturas perfeitamente referenciados, o que não foi feito. Elas provavelmente carregarão a aparência padrão de "no-grade" ou causarão crash.
3. **Tattoos (`etcitemgrp.txt` ou `armorgrp.txt`)**:
   Completamente ausentes dos arquivos de sistema do cliente.

## Scope
### In Scope
- **Refatorar as Royal Weapons (99300-99315)** no `weapongrp.txt`, espelhando 1:1 a estruturação das armas Dynasty originais (mesmo mesh, mesmo som, mesmo efeito de soulshot).
- **Refatorar os Royal Armors (99200-99224)** no `armorgrp.txt`, espelhando a estruturação exata das armaduras originais de base (Dynasty ou Moirai) para garantir que apareçam corretamente em todas as raças.
- **Incluir as Tattoos Customizadas (41001 a 41024)** no `itemname-e.txt` com seus respectivos nomes (ex: *Tattoo of Ogre - Lv 6*).
- **Incluir as Tattoos no `armorgrp.txt`** (Slot Underwear), apontando para o ícone correto (`icon.etc_str_hena_i00`) e sem malhas 3D para não bugar o corpo do personagem (comportamento padrão de underwear).

### Out of Scope
- Criação de novos ícones personalizados do zero no pacote UTX. Serão re-utilizados ícones existentes do jogo (Dynasty e Hennas).

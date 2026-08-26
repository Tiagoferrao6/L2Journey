## Abordagem de Correção
O método será localizar no client os itens base (ex: armas Dynasty base 9442, etc.) e copiar sua estrutura integral para os IDs correspondentes Royal (99300+), ajustando apenas o `ID` inicial e os nomes de exibição. 

Para as Tattoos (41001-41024), será utilizada a base de uma roupa de baixo existente ou item sem renderização visual 3D (para evitar crashes no Paperdoll), alterando apenas o ícone de exibição.

### Royal Weapons (`weapongrp.txt`)
- Iremos limpar as linhas `99300` a `99315` quebradas atuais (que estão no formato de armadura).
- Iremos espelhar as linhas das armas originais Dynasty (Great Sword, Dagger, Bow, etc.). Exemplo: copiar a linha do ID `9442` (Dynasty Blade), e recriar com o ID `99300`.

### Royal Armors (`armorgrp.txt`)
- Iremos remover as linhas defeituosas `99200` a `99224`.
- Encontraremos os itens originais do set (ex: Dynasty Breastplate) que possuem a formatação correta de meshes (Human Fighter, Dark Elf, etc).
- Duplicaremos essas linhas para os IDs 99200+, garantindo que a renderização do set Royal in-game fique impecável para todas as raças.

### Tattoos Customizadas
- **`armorgrp.txt`**: Vamos adicionar IDs 41001 a 41024. Slot de `underwear`. Ícones: `icon.etc_str_hena_i00` (Ogre), `icon.etc_dex_hena_i00` (Monk), `icon.etc_str_hena_i01` (Assassin), `icon.etc_str_hena_i02` (Blood).
- **`itemname-e.txt`**: Vamos gerar 24 novas entradas com a descrição "Custom Tattoo that massively increases attributes. Can be leveled up." e o nome apropriado baseado no XML do servidor (`tattoos.xml`).

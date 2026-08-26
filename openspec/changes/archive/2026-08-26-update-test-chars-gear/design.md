## Arquitetura de Skills Acumulativas e Certificações
- O sistema lerá as configurações da tabela `character_skills` em `z_custom_test_characters_setup.sql`.
- **TitanTester**: Terá todas as skills chave de Titan e DreadNought garantidas na inserção, preferencialmente nível máximo e enchantadas. As skills de certificação de subclasses (nível 80/85) também serão inseridas.
- **SilverTester**: Mesma abordagem, mas englobando as skills de Moonlight Sentinel e Sword Muse (Songs já presentes, mas agora reforçadas e enchantadas).

## Gerenciamento de Inventário (Tabela `items`)
- **Limpeza**: Será usado `DELETE FROM items WHERE owner_id IN (300000000, 300000001);` para limpar o inventário anterior.
- **Sets e Armas Royal**: Serão inseridos itens com `enchant_level = 6`, `loc = 'INVENTORY'` (ou equipados), englobando todos os itens listados no request. Atributos (Element) serão adicionados utilizando a tabela `item_elementals` ligando os `object_id` (se suportado pelo script), ou ignorado se o servidor assumir default pela interface (porém geralmente é inserido em `item_elementals`).
- **Tattoos Customizadas**: As tattoos (ID 41006 Ogre, 41012 Monk, 41018 Assassin, 41024 Blood) serão inseridas com Level 6 no inventário de cada personagem.
- **Itens de Acesso (Quests)**: 
  - Baium: Blooded Fabric (ID 4295)
  - Valakas: Floating Stone (ID 3865)
  - Antharas: Portal Stone (ID 7267)
- **Consumíveis de Combate e Moedas**: 
  - **Moedas**: 1 Bilhão de Adena (57), 500 Raid Coins.
  - **Reagentes**: Soul Ore (1785), Spirit Ore (3031), Crystal: S Grade (1462), Battle Symbol.
  - **Poções e Scrolls**: Greater Healing Potion (1539), Mana Potion (728), Greater CP Potion (5592), Blessed Scroll of Escape (1538), Scroll of Escape (736).
  - **Shots e Munição**: Soulshots S (1467), Blessed Spiritshots S (3952) e Flechas S-Grade para a SilverTester.

## Trade-offs Considerados
- **Volume de Inserções**: O script SQL se tornará bem maior com a adição de todas as habilidades (uma vez que não há um sistema automático no DB para auto-learn offline).
- **Atributos de Elemento nas Armas/Set**: Se for inserido via SQL direto, será necessário criar queries extensas para a tabela `item_elementals` (com os valores máximos 120 para armaduras, 300 para armas). Caso não existam os object_ids fixos pré-inseridos ali, será preciso orquestrar o script cuidadosamente com `SET @start_id`.

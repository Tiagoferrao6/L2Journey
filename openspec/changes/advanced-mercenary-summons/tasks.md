## 1. Engine de Templates

- [ ] 1.1 Criar a estrutura base de `MercenaryData` (Parser XML) 
- [ ] 1.2 Criar um arquivo XML de exemplo em `data/xml/mercenaries.xml` com o template do "Mini-Golkonda"
- [ ] 1.3 Criar a classe `MercenaryTemplate` contendo base stats, skills e equipment.

## 2. A Classe MercenaryInstance

- [ ] 2.1 Criar `L2MercenaryInstance` extendendo de `L2Summon`
- [ ] 2.2 Sobrescrever o pacote de broadcast de rede (`NpcInfo` / `PetInfo`) para assegurar que ele envie os visual states corretos baseados no template.
- [ ] 2.3 Atrelar a barra de controle nativa de pets para funcionar e movimentar o mercenário

## 3. O Item Consumível

- [ ] 3.1 Criar o script/handler `ItemHandler` (ex: `MercenaryContract.java`) para reagir a um duplo-clique de item no inventário
- [ ] 3.2 Implementar bloqueio se o usuário já possuir um summon ativo
- [ ] 3.3 Adicionar lógica para remover (consumir) o item e spawnar o `L2MercenaryInstance` no mundo

## 4. Integração de Combate

- [ ] 4.1 Modificar as classes de ataque para calcular danos, crítico e casting speeds baseados no `MercenaryTemplate`
- [ ] 4.2 Testar a morte e o desaparecimento correto (unsummon)
- [ ] 4.3 Fazer o spawn final e testar os botões de Atacar, Seguir e Parar in-game

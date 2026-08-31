## Context

No L2Journey, a criação de Summons é rígida e dependente de templates na tabela de `npc`. Jogadores gostariam de evocar companheiros personalizáveis que funcionem como pets (com barra de comandos), mas que pareçam outros jogadores, NPCs únicos ou Bosses, permitindo que a equipe de administração crie itens como "Contrato de Mercenário: Guardião Elfo" (que evoca um Elfo curandeiro) ou "Mini-Golkonda" (que ataca em área). 

## Goals / Non-Goals

**Goals:**
- Criar a classe `L2MercenaryInstance` que herda de `L2Summon`.
- Estabelecer um parser XML próprio (`MercenaryData.java`) para carregar a definição visual e de atributos desses mercenários.
- Permitir que itens consumíveis (ItemHandlers) leiam esse XML e gerassem a invocação correta.

**Non-Goals:**
- Mercenários não usarão equipamentos de forma dinâmica (inventory). Eles terão armas e armaduras "pintadas" (hardcoded no template) apenas para efeito visual.
- Eles não ganharão XP próprio como Pets normais. Eles acompanham o nível do player ou têm um nível estático fixo pelo contrato.

## Decisions

**1. Herança de L2Summon vs L2Pet**
- *Decisão:* `L2MercenaryInstance extends L2Summon`.
- *Rationale:* `L2Pet` possui amarras fortes com banco de dados (tabela `pets`) para salvar fome, XP, level e inventário. Como queremos Mercenários descartáveis ou estáticos atrelados a um contrato temporário, `L2Summon` é mais leve, não requer persistência de banco de dados e já possui a janela de controle ativa.

**2. O Parser XML de Templates**
- *Decisão:* Criar `data/xml/mercenaries.xml` que define IDs customizados, que não conflitam com NPCs normais (ex: a partir de 90000). 
- *Rationale:* Misturar Mercenários na tabela normal de NPCs torna a manutenção confusa. Um XML dedicado permite estruturar `<skills>` e `<equipment>` de forma limpa. O pacote `NpcInfo` (ou `PetInfo` dependendo de como o cliente trata) enviado à rede forçará o `displayId` (ex: ID visual do Golkonda) sem precisar criar um NPC real na database.

## Risks / Trade-offs

- **[Bugs Visuais no Cliente]** O cliente pode tentar enviar pacotes de animação que um modelo de player possui, mas um modelo de Boss não.
  - *Mitigação:* Usar os templates base do cliente para garantir compatibilidade.
- **[Overpowered Pets]** Summons com atributos de Boss quebrarem o PVP.
  - *Mitigação:* O XML deve ser cuidadosamente balanceado pela staff. Pode-se restringir a evocação de mercenários apenas para instâncias PvE ou remover o "Contrato" após a morte do Mercenário (consumível).

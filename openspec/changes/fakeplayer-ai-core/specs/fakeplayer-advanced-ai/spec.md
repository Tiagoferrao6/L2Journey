## ADDED Requirements

### Requirement: Consumo Inteligente de Poções
A IA do FakePlayer DEVE checar periodicamente seu HP/MP e consumir poções do inventário (ou itens emulados) quando a métrica cair abaixo de um limiar configurado.

#### Scenario: HP Baixo em Combate
- **WHEN** o HP do FakePlayer cai abaixo de 40% durante um combate
- **THEN** ele consome uma Greater Healing Potion, se o cooldown permitir

### Requirement: Perfis de Combate Diferenciados
A engine DEVE suportar instâncias diferentes de IA, como Healers e Tanks, onde a prioridade de ações diverge da prioridade padrão de dano.

#### Scenario: Avaliação de Ação do Healer
- **WHEN** o FakeHealerAI chega na etapa de "Think"
- **THEN** ele avalia primeiro o HP dos membros do grupo (party/leader) antes de decidir atacar um inimigo

### Requirement: Uso Automatizado de Soulshots
O FakePlayer DEVE gerenciar automaticamente o buff de Soulshots e Spiritshots quando atacar ou usar magias, simulando o comportamento de auto-use de um player real.

#### Scenario: Uso de Magia
- **WHEN** um FakePlayer foca em disparar um projétil mágico
- **THEN** a engine ativa a lógica do Spiritshot imediatamente antes da skill iniciar

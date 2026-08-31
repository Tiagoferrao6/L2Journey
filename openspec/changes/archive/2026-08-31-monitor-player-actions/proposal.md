# Monitoramento e Auditoria de Ações do Player (Packet Logger)

## Objetivo
Criar um sistema de auditoria profunda ("Player Action Logger") que intercepte, registre e serialize toda a comunicação de rede entre o Cliente (jogador) e o Game Server. O foco é registrar exatamente o que o jogador faz (ações) e o que ele percebe do ambiente (estado enviado pelo servidor).

## O que vamos capturar?

Ao registrar tudo que o cliente envia (Incoming Packets) e tudo que o servidor responde (Outgoing Packets), teremos duas visões complementares:

### 1. O que o Cliente envia (Ações do Player)
São os pacotes `IClientIncomingPacket`. Capturaremos coisas como:
- **Movimentação:** Onde o player clicou para andar (`Action`, `MoveBackwardToLocation`).
- **Combate e Habilidades:** Quem ele selecionou como alvo, que magia tentou castar (`RequestMagicSkillUse`, `AttackRequest`).
- **Interação:** Quais itens usou do inventário (`RequestUseItem`), quais diálogos de NPC clicou (`RequestBypassToServer`).
- **Social:** O que digitou no chat, quais macros usou.

### 2. O que o Servidor responde (Ambiente e Estado)
São os pacotes `IServerOutgoingPacket`. Capturaremos coisas como:
- **Visão do Ambiente:** NPCs e outros players que apareceram na tela dele (`NpcInfo`, `CharInfo`, `SpawnItem`).
- **Feedback de Combate:** Dano que ele causou, ataques que esquivou, skills que falharam (`Attack`, `MagicSkillUse`, `SystemMessage`).
- **Estado Interno:** Atualizações de HP/MP/CP, mudanças no inventário, buffs recebidos (`UserInfo`, `StatusUpdate`, `ItemList`).

## Como os dados serão armazenados?

Dado o altíssimo volume de dados (milhares de pacotes por minuto por jogador ativo), o armazenamento direto em Banco de Dados Relacional (MySQL/MariaDB) de forma síncrona **vai derrubar a performance do Game Server**.

**Estratégia Proposta:**
- Criar um **Logger Assíncrono** em arquivo texto estruturado (formato JSON Lines ou CSV) gravado no disco (ex: `log/player_actions/NOME_DO_PLAYER_DATA.log`).
- Opcionalmente, enviar esses dados para um banco de dados NoSQL (como MongoDB ou ElasticSearch) ou uma fila (Kafka/RabbitMQ) se o objetivo for criar dashboards em tempo real (Kibana/Grafana).

## O que provavelmente vamos ver com esses dados? (Casos de Uso)

Se lermos o log de um player após algumas horas de jogo, veremos a "história" exata da sessão dele. Por exemplo:

1. **Detecção de Bots e Automação:**
   - Veremos um padrão robótico: O servidor envia `NpcInfo` (monstro apareceu) -> Exatos 10ms depois o cliente envia `Action` (target) -> 10ms depois `RequestMagicSkillUse`. Se os tempos de reação forem impossíveis para um humano e excessivamente repetitivos, é um bot.
2. **Reconstrução de Bugs (Time-Travel Debugging):**
   - O player reclama que "perdeu um item". No log, veremos o servidor enviando `ItemList`, seguido do cliente enviando `RequestDropItem` (ele jogou no chão sem querer) e em seguida a confirmação do servidor.
3. **Auditoria de Exploits (Bugs de HTML/Bypass):**
   - Jogadores maliciosos injetam pacotes forjados para comprar itens grátis ou puxar buffs de NPCs longe. No log, veremos um `RequestBypassToServer` para um NPC que não estava perto dele (não houve `NpcInfo` daquele NPC enviado a ele recentemente).
4. **Comportamento da IA e Bugs de Geodata:**
   - Veremos que o jogador tentou atacar (`Action`), mas o servidor respondeu com `ActionFailed` ou um `SystemMessage` de "Cannot see target", indicando que uma parede invisível (Geodata) bloqueou a ação.

## Decisões em Aberto (Para o Usuário)

1. **Onde você prefere salvar inicialmente?** Apenas arquivos `.log` em JSON para ingestão futura, ou quer que o servidor faça inserts direto no MySQL (mesmo sabendo do custo de performance)?
2. **Nível de Detalhamento:** Queremos salvar o raw byte array do pacote (para debug hardcore) ou apenas fazer o *parse* para JSON dos pacotes mais importantes (Movimento, Combate, Bypass, Chat, Items)?

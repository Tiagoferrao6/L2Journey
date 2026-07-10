# Capability: fake-players-poc

## Purpose
(TBD) Povoamento de bots laboratório (MVP) e validação da arquitetura de IA (persistência, otimização e testes acelerados).

## Requirements

### Requirement: Povoamento Laboratorial de Bots
O sistema DEVE instanciar exatamente 10 bots em locais específicos da região de Gludio para validar o MVP.

#### Scenario: Distribuição dos Bots
- **WHEN** o servidor inicia em modo laboratório
- **THEN** devem existir 2 Traders em Gludio (um com itens fixos e um com itens variáveis)
- **THEN** devem existir 4 Hunters na zona Death Pass caçando monstros agressivos
- **THEN** devem existir 4 Hunters na zona Ruins of Despair caçando mortos-vivos

### Requirement: Persistência de Dados de Fake Players
O banco de dados DEVE armazenar as características definidoras de cada Fake Player na tabela `fake_players_profiles`.

#### Scenario: Estrutura da Tabela
- **WHEN** o banco de dados é inicializado
- **THEN** a tabela `fake_players_profiles` possui colunas para `agressividade` (1-10), `coragem` (1-10), `classe`, e `turno`

### Requirement: Otimização por Zone Listener
O sistema DEVE ativar o processamento da inteligência artificial dos bots apenas quando houver um jogador humano próximo.

#### Scenario: Ativação por Presença
- **WHEN** o jogador principal entra nas zonas Gludio, Death Pass ou Ruins of Despair
- **THEN** os bots dessas regiões começam a processar suas lógicas de forma integral
- **WHEN** o jogador sai das zonas ativas
- **THEN** a lógica detalhada dos bots é desativada e eles são suspensos ou processados simplificadamente

### Requirement: Ciclos de Teste Acelerado
Os bots DEVEM usar agendamentos (schedules) acelerados para permitir a validação rápida de eventos vitais (SoE, despawn, reset de inventário).

#### Scenario: Renovação Rápida
- **WHEN** um trader realiza seu ciclo
- **THEN** ele renova seu inventário e recalcula preços de hora em hora
- **WHEN** um hunter completa seu turno curto
- **THEN** ele executa o fluxo de Scroll of Escape ou despawn para testar o comportamento

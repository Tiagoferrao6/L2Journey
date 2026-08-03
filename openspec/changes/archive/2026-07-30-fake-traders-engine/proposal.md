# Fake Traders Engine

## What
Um novo sistema de Bots de Comércio (Fake Players) que herdam diretamente da classe `Player`. Esse sistema dará vida à economia do servidor criando lojas autônomas de Venda (SELL), Compra (BUY) e Manufatura (CRAFT), operando sob regras estritas de horário (Turnos/Janelas de Atividade) e configurações baseadas em perfis XML.

## Why
Atualmente, o mercado de jogadores é escasso quando a população do servidor está baixa ou em servidores privados pequenos. Usar NPCs padrão para suprir itens quebra a imersão. Ao usar `FakePlayers` que imitam jogadores reais (inclusive sentando para abrir a *Private Store* e *Private Manufacture*), o mundo do Lineage 2 ganha muita vida, as cidades principais (como Gludio) parecem ativas, e o GM consegue controlar a inflação através das dinâmicas de Adena Sink (destruição) e Adena Faucet (geração).

## Capabilities
### New Capabilities
- `fake-traders-engine`: O core que estende `Player` para `FakePlayer`, permitindo lojas nativas sem reescrever pacotes do cliente.
- `fake-traders-economy`: O leitor de XML que interpreta `fake_traders_economy.xml` (os perfis de preços e itens/receitas) e `fake_traders_spawns.xml` (os bots em si, suas cidades, aparências e títulos).

## Impact
- **Mecânicas Core**: Como os bots herdarão de `Player`, toda interação (trade, craft) será nativa. O único impacto é que o motor precisa injetar Adena (para os bots BUY) e deletar Adena no fim do turno (para os bots SELL/CRAFT).
- **Banco de Dados**: Nenhum! Optou-se por usar XMLs em vez do Banco de Dados para a definição inicial, permitindo recarregamento rápido via comando admin, além da natureza efêmera do inventário do bot.
- **Relógio (Clock)**: O `FakeTraderManager` rodará uma task agendada verificando a *janela de atividade* (`renewTime`) de cada bot. Ao expirar, o bot fecha a loja, desaparece (ou recria o inventário) e abre novamente com 1 a 3 itens aleatórios do seu perfil XML.

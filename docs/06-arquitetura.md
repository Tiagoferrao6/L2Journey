# Arquitetura do Sistema e Estratégia de Desenvolvimento

## Topologia de Rede L2Journey (Single Player)
O ecossistema é desenhado para rodar localmente no PC do usuário:
- **Game Server**: Responsável por toda a lógica de jogo, simulação do `FakePlayerManager` e das IA dos bots. (Processo Java pesado).
- **Login Server**: Processo leve para autenticação.
- **MariaDB Database**: Armazena as contas, itens e a nova tabela `fake_players_profiles`.
- **Client (L2.exe)**: O jogo executado pelo jogador conectando ao Login local (127.0.0.1).

## Estratégia de Desenvolvimento IA vs Humano
O desenvolvimento deste projeto será um esforço conjunto entre humanos e Agentes de IA.

1. **Ambiente de Edição (IA)**:
   - A IA (Agente) utilizará ferramentas de IDE para gerar código, escrever documentações no diretório `/docs` e criar planos de implementação estruturados.
   - Todo o conhecimento gerado pela IA deve ser formalizado em `.md` nesta pasta, para garantir que futuras execuções mantenham o contexto arquitetural.

2. **Deploy e Teste (VS Code / Humano)**:
   - Compilação e inicialização do servidor.
   - Teste In-Game (abrir o client, logar e verificar se a IA do Fake Player está executando corretamente).
   - Ajustes finos nos pesos de DNA com base no "feel" da gameplay.

## Core Lógico
- `FakePlayerManager`: O cérebro macro. Usa "Zone-Listeners" para só processar lógica quando o jogador humano estiver fisicamente próximo ou na mesma região.
- `BaseAI` -> `FakeHunterAI` / `FakeTraderAI`: O cérebro micro. Define as ações individuais frame-a-frame de cada bot.

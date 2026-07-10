## Context

O projeto 'Lineage 2 Single Player' baseado no L2Journey ainda não possui uma estrutura clara de documentação técnica para definir arquitetura, modelos de dados, interfaces e casos de uso de maneira centralizada. Isso dificulta a governança e a manutenção do projeto a longo prazo.

## Goals / Non-Goals

**Goals:**
- Estabelecer a estrutura do diretório `/docs`.
- Inicializar templates básicos em markdown para guiar os colaboradores no preenchimento das especificações e da arquitetura do projeto.
- Centralizar o conhecimento técnico em um único diretório para o repositório.

**Non-Goals:**
- Não iremos preencher o conteúdo detalhado de cada arquivo neste momento; o objetivo é apenas inicializar a estrutura e os templates.
- Não iremos modificar nenhuma lógica ou código-fonte.

## Decisions

- **Uso de Markdown (.md):** Escolhido devido à facilidade de leitura e suporte nativo em plataformas como GitHub.
- **Convenção de Nomenclatura:** Arquivos numerados sequencialmente (`01-visao.md`, `02-requisitos.md`, etc.) para garantir uma ordem lógica de leitura, seguindo padrões comuns de documentação de software.

## Risks / Trade-offs

- **Risco:** Documentação pode se tornar desatualizada se os desenvolvedores não tiverem a disciplina de mantê-la sincronizada com o código.
  - *Mitigação:* Integrar verificações de documentação (como linting ou lembretes no PR) em estágios futuros do projeto e definir a atualização da documentação como parte dos critérios de aceite.

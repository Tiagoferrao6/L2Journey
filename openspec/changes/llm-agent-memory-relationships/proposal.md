# Proposal: Sistema de Memória & Relacionamento (Rivalidades e Amizades)

## Summary
Implementar um sistema de memória persistente baseada em grafo/vetorial para os bots LLM (FakeHunters) no L2Journey. Cada bot manterá um histórico emocional e comportamental de suas interações com jogadores humanos e outros bots (afeto, rivalidades, vinganças, gratidão e dívidas de ajuda).

## Motivation
Atualmente os bots em MMORPGs agem sem contexto histórico de suas relações sociais. Ao adicionar uma memória vetorial/RAG (ou tabela relacional de reputação) para cada bot, eles poderão:
- Reconhecer jogadores que os ajudaram em combate (ex: cura, resgate ou party) e retribuir a ajuda no futuro.
- Guardar rancor de jogadores que roubaram seus mobs (KS), agrediram em Flag/PK ou deram "trash talk", recusando trocas ou juntando o clã para vingança.
- Desenvolver "personalidades sociais" únicas e evolutivas.

## Proposed Changes
- **Memory Store**: Tabela MySQL `character_llm_memories` e `character_llm_relationships` para gravar eventos sociais com timestamp, nível de afinidade (-100 a +100) e resumo sintético do evento.
- **RAG / Memory Retrieval Engine**: Injetar no prompt do Gemini/Ollama as últimas N memórias relevantes sobre o jogador com quem o bot está interagindo no momento.
- **Decision Influencer**: Modificar o gerador de prompts para ajustar a postura do bot (Amigável, Neutro, Hostil, Vingativo) de acordo com o score de relacionamento.

## Verification
- Testes unitários para persistência e recuperação de memórias por `target_player_id`.
- Teste de integração: Simular um jogador roubando mob do bot e verificar a queda do score de afinidade de +0 para -30 e a recusa imediata de convite para party.

# Visão: Projeto L2 Single Player

## O Projeto
O L2Journey está passando por uma transformação arquitetural para se tornar um ecossistema Single Player autossuficiente. O objetivo é permitir que um único jogador experimente o mundo de Lineage 2 como se estivesse em um servidor populado e vivo.

## Focos Principais
1. **Fake Players Persistentes**: Bots não são mais apenas "mobs que parecem players". Eles possuem "DNA" comportamental (agressividade, coragem), inventários que mudam com o tempo, e estado salvo no banco de dados.
2. **Economia Dinâmica**: Traders ajustam preços baseados no que vendem e compram, renovando estoques em ciclos definidos (Trade-Cycles).
3. **Simulação de IA em Gludio**: O escopo inicial (MVP) para testar esta fundação de IA foca na região de Gludio, introduzindo 30 Traders e 30 Hunters que reagem à presença do jogador real.

## Como as IAs devem ler este documento
Qualquer agente (LLM/IA) auxiliando no desenvolvimento deste projeto DEVE consultar a pasta `/docs` antes de sugerir soluções arquiteturais. Este diretório é a **fonte da verdade** do estado atual do sistema Single Player.

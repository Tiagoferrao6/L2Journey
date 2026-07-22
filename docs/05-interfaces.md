# Interfaces e Protocolos

## Interfaces de Interação (Player-Bot)
1. **Chat / IA**: Integrações futuras permitirão que os Fake Players respondam via Chat usando ganchos (hooks) em sistemas de LLM leves ou scripts de conversação pré-definidos (baseados em Regex).
2. **Trade Interface**: Fake Traders usarão a interface nativa de "Private Store" do Lineage 2, não exigindo mods no client.

## Protocolo de Rede (Client-Server)
Para que os bots pareçam idênticos a jogadores reais aos olhos do Client do jogo, as seguintes adaptações de rede são necessárias:
- O servidor enviará `CharInfo` (pacote usado para players) em vez de `NpcInfo` (usado para monstros/NPCs) para representar visualmente o Fake Player.
- **Movimentação**: Pacotes `MoveToLocation` e `StopMove` idênticos aos gerados por players.
- **Visual**: Equipamentos, Soulshots visuais e Castings devem gerar pacotes broadcast padrões.

## Painel Administrativo
- Futuramente, um painel web ou interface In-Game (`//admin_fake_players`) será implementado para gerenciar as rotinas de spawn/despawn, ajustar pesos de DNA em tempo real e visualizar o status do ecossistema econômico (Trade-Cycles).

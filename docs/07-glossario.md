# Glossário Técnico

| Termo | Definição / Significado no Projeto |
|---|---|
| **Fake Player / Bot** | Personagem simulado pelo servidor que usa o pacote de rede `CharInfo`, aparentando ser um jogador real para o Client L2. |
| **DNA** | Conjunto de pesos comportamentais (atributos ocultos) atribuídos a um bot para gerar variedade na tomada de decisão (agressividade, coragem, etc). |
| **DNA-Weight** | Valor numérico, geralmente de 1 a 10, usado nos cálculos condicionais da IA. Ex: Se Coragem = 2, o bot foge ao atingir 40% de HP. |
| **Trade-Cycle** | Ciclo econômico oculto. Período configurável (ex: 48h in-game) após o qual um Fake Trader reseta sua lista de vendas/compras e reavalia preços. |
| **Zone-Listener** | Componente de backend que dispara um evento quando um jogador (real) entra ou sai de um polígono delimitado no mapa (uma "Zona"). Usado para ligar/desligar a IA dos bots localmente. |
| **Schedule** | A jornada diária de um bot. Ex: "Este bot só aparece online entre 14:00 e 22:00". Se fora do horário, o bot é despawnado. |
| **CharInfo / NpcInfo** | Pacotes nativos do protocolo de rede do Lineage 2. O servidor deve enganar o Client mandando CharInfo para os bots, para renderizá-los com texturas, nomes e clãs de players. |

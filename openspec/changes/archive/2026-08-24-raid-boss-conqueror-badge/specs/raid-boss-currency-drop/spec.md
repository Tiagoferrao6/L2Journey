# Capability: Raid Boss Currency Drop

## ADDED Requirements

### Requirement: Range Drop Calculation
The server MUST identify when any open world `RaidBoss` or `GrandBoss` is defeated and calculate the number of `Conqueror's Badge` coins to drop based on the boss level range.

#### Scenario: Drop de Moedas por Faixa de Nível do Boss
- **GIVEN** que um jogador abateu um Raid Boss ou Grand Boss no mundo aberto
- **WHEN** o servidor processar a morte do boss
- **THEN** a quantidade de moedas MUST ser sorteada segundo o nível do boss:
  - Nível 20 a 39: 1 a 3 moedas (`Rnd.get(1, 3)`)
  - Nível 40 a 51: 4 a 8 moedas (`Rnd.get(4, 8)`)
  - Nível 52 a 60: 10 a 18 moedas (`Rnd.get(10, 18)`)
  - Nível 61 a 75: 20 a 35 moedas (`Rnd.get(20, 35)`)
  - Nível 76 a 85: 40 a 70 moedas (`Rnd.get(40, 70)`)

### Requirement: Ground Drop Spawning
Upon calculating the currency quantity, the server MUST execute the ground drop method (`attackable.dropItem(killer, itemId, count)`) at the defeated boss location.

#### Scenario: Dropar o Item no Chão
- **GIVEN** que a quantidade de moedas foi calculada
- **WHEN** o boss for finalizado
- **THEN** o item da moeda MUST ser instanciado no chão no local exato do abate para coleta.

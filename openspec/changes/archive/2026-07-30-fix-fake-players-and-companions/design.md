# Technical Design: FakePlayers Equipment, Dual Shots, Companion Party AI, Field Simulation & Dashboard Control Engine

## Architecture & Systems Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│          FakePlayer, Mercenary Companion & Dashboard Control Engine         │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
     ┌───────────────────┬─────────────┼───────────────────┬───────────────────┬───────────────────┐
     ▼                   ▼             ▼                   ▼                   ▼                   ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  Equipamento │  │  Injeção     │  │ Caminhada GK │  │ Healer Party │  │ Simulação    │  │ Painel GM    │
│   por Grade  │  │ Dupla Shots  │  │ em Cidade    │  │ Skills/Grade │  │ Fiel PvE/PvP │  │ Controles Bot│
│ (NG até S)   │  │(SS + B.SShot)│  │ (Walk to GK) │  │ (Toda Party) │  │  em Campo    │  │ (Edit/Tele/Lvl│
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
```

## 1. Modelo Arquitetural do Companion
- **Modelo Híbrido Escolhido (`MercenaryInstance` + Party Member + Auto-Teleport + UI HTML)**:
  - Mantém o Mercenário como um `Player` humanóide completo na Party (`Party.addPartyMember(merc)`).
  - Permite monitorar e curar/buffar **todos os membros da Party** (não apenas o Mestre).
  - Renderiza armaduras S/A/B/C/D Grade top, duals, cajados e joias com glow.
  - Possui **Painel de Gerenciamento HTML** (`.companion`) e **Auto-Teleporte Instantâneo** com o mestre.

## 2. Matriz de Equipamentos Top por Grade

| Grade | Níveis | Armor Heavy | Armor Light | Armor Robe | Armas Top da Grade |
| :--- | :---: | :--- | :--- | :--- | :--- |
| **No-Grade** | 1 - 19 | Wooden Set | Wooden Set | Devotion Set | Short Sword, Bow, Apprentice Staff |
| **D-Grade** | 20 - 39 | Brigandine Set | Manticore Set | Elven Mithril Set | Tarbar, Elven Bow, Staff of Life, Atuba Hammer |
| **C-Grade** | 40 - 51 | Full Plate Set | Plated Leather Set | Carmian / Demon Set | Berserker Blade, Eminence Bow, Homunkulus |
| **B-Grade** | 52 - 60 | Zubei Heavy | Blue Wolf Light | Avadon Robe | Great Sword, Bow of Peril, Valhalla, Lance |
| **A-Grade** | 61 - 75 | Tallum Heavy | Dark Crystal Light | Dark Crystal Robe | Tallum Blade, Soul Bow, Dasparion's Staff |
| **S-Grade** | 76 - 85 | Imperial Crusader | Draconic Leather | Major Arcana Robe | Heaven's Divider, Draconic Bow, Arcana Mace |

## 3. Injeção Dupla de Shots (Soulshot + Blessed Spiritshot)
- Ativar **Soulshot** para todos os ataques normais e habilidades físicas.
- Ativar **Blessed Spiritshot** para todas as magias, buffs e cura.
- Ambos operam simultaneamente de forma automatizada e sem consumo de itens do inventário.

## 4. Algoritmo de Despacho em Cidade (Caminhada até a Gatekeeper)
- O bot calcula a posição do NPC Gatekeeper mais próximo na cidade.
- Executa movimentação realista (`moveToLocation`) caminhando até o NPC Gatekeeper (raio < 120).
- Ao chegar na Gatekeeper, dispara o efeito de teleporte e viaja instantaneamente para a zona de caça (estado `HUNTING`).

## 5. Painel de Controle de FakePlayers no Dashboard GM (`web/index.html`)

### 5.1 Endpoint `POST /api/admin/fakeplayers/edit`
Permite ao GM inspecionar e alterar as propriedades de qualquer bot individual:
```json
{
  "botName": "HunterOne",
  "action": "UPDATE_BOT",
  "active": true,
  "level": 76,
  "grade": "S_GRADE",
  "behaviorState": "HUNTING",
  "teleport": {
    "target": "ZONE",
    "zoneName": "Giran Castle Town",
    "x": 83400,
    "y": 147900,
    "z": -3400
  },
  "reloadXml": false
}
```

### 5.2 Interface do Usuário no Dashboard:
- **Tabela com Modal/Drawer de Edição por Bot**:
  - 📍 **Localização Atual**: Exibe Zona e Coordenadas (X, Y, Z).
  - 🚀 **Alterar Localização**: Dropdown de Cidades/Zonas, botão "Mover Bot para GM" e entrada manual X, Y, Z.
  - ⚡ **Ativar / Desativar**: Toggle switch para spawnar ou colocar o bot em Sleep/Despawn.
  - 🔄 **Recarregar Bot**: Botão para forçar reload do perfil/XML do bot sem reiniciar o servidor.
  - 🧠 **Comportamento (Behavior)**: Selector de Stance (`HUNTING`, `SAFETY_FLEE`, `PAUSE_IDLE`, `PVP_AGGRESSIVE`, `SELLING`, `FARM_SOLO`).
  - 📊 **Nível e Equipamento**: Slider de Nível (1 a 85) e Selector de Grade (`NG`, `D`, `C`, `B`, `A`, `S`) que força re-equipamento instantâneo.

## 6. Catálogo de Skills do Companion Healer por Grade & Cura de Party
- Monitora a Party inteira (`owner.getParty().getMembers()`) e aplica curas/buffs.
- Catálogo de skills por grade: NG, D, C, B, A, S.

## 7. Simulação Fiel em Campo (PvE & PvP AI Engine)
- Target acquisition inteligente, kiting para arqueiros/magos, retaliação anti-gank PvP, coleta de loot (1-3s pause) e sentar (`sitDown`) para regerar MP < 10%.

## 8. Tracking de Chat & Filtro por Região
- `Say2.java` envia `x`, `y`, `z` e `regionName` para o `WebAPIManager.addChatMessage`.
- Terminal de Chat no Dashboard exibe `[ALL] PlayerName (@Gludio Town [-14200, 123100, -3100]): "Mensagem"`.
- Filtro Select por Região no Dashboard Web.

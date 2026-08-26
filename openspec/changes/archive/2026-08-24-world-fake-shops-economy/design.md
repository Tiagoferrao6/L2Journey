## Context

Para estruturar a distribuição comercial de acordo com as diretrizes refinadas, este design define o mapa completo do mundo com 15 lojas em Gludio, 15 lojas em Dion, 50 lojas em Giran (cobrindo C a S Grade com encantados) e 20 lojas em Aden (foco em S80/S84 e pedras de elemento).

## Goals / Non-Goals

**Goals:**
- **Gludio (15 Lojas)**: Mercado de transição C e D-Grade.
- **Dion (15 Lojas)**: Mercado de transição C e D-Grade.
- **Giran Mega Hub (50 Lojas)**: Comércio completo cobrindo **C, B, A e S-Grade**, incluindo armas, sets e itens encantados (+4 a +8), refinados, oficinas e compradores.
- **Aden Capital de Elite (20 Lojas)**: Foco total em **S80 (Dynasty, Icarus)** e **S84 (Vesper Noble, Vorpal, Elegy)** encantados (+4 a +8), Pedras de Atributo Elementar (Attribute Stones & Crystals), Enchants Blessed S e Life Stones Lvl 80/84.
- **Feiras Locais (5 Lojas por cidade)**: Oren, Hunters, Heine, Goddard, Rune, Schuttgart.

**Non-Goals:**
- Venda de itens S84 em vilas iniciantes.

## Decisions

### 1. Diagrama de Distribuição Comercial por Capitais

```
                         ┌─────────────────────────────────────────┐
                         │   ADEN CASTLE TOWN (Capital de Elite)   │
                         │       20 FakeShops S80 & S84 (+4/+8)    │
                         │   Elegy, Vorpal, Vesper, Icarus, Dynasty
                         │   Attribute Stones/Crystals (Elemento)  │
                         │   Blessed Enchants S, Top Life Stones   │
                         └────────────────────┬────────────────────┘
                                              │
                                              ▼
                         ┌─────────────────────────────────────────┐
                         │  GIRAN TOWN (Mega Hub C ate S Grade)    │
                         │       50 FakeShops C / B / A / S Grade  │
                         │  Sets Encantados (+4/+8), Oficinas, BUY │
                         └────────────────────┬────────────────────┘
                                              │
                                              ▼
                         ┌─────────────────────────────────────────┐
                         │  GLUDIO & DION (Mercados de Transicao)  │
                         │    15 Lojas em Gludio | 15 Lojas em Dion │
                         │             D-Grade e C-Grade           │
                         └────────────────────┬────────────────────┘
                                              │
                                              ▼
     ┌───────────────────────────────────────────────────────────────────────────┐
     │                     CIDADES SECUNDÁRIAS (5 Lojas cada)                   │
     │            Oren  •  Hunters  •  Heine  •  Goddard  •  Rune  •  Schuttgart    │
     │             Foco Estrito: Suprimentos Básicos & Materiais Primários      │
     └───────────────────────────────────────────────────────────────────────────┘
```

### 2. Tabela de Distribuição Comercial por Cidade

| Cidade | Função Econômica | Qtd Lojas | Conteúdo do Catálogo | Coordenadas Base (X, Y, Z) |
|---|---|:---:|---|---|
| **Gludio** | Transição C e D-Grade | **15** | D-Grade & C-Grade básico, Shots D/C, Consumíveis | `-14228, 123445, -3115` |
| **Dion** | Transição C e D-Grade | **15** | D-Grade & C-Grade básico, Shots D/C, Consumíveis | `15632, 142876, -2705` |
| **Giran** | Mega Hub (C a S Grade Encantados) | **50** | C, B, A e S-Grade com Sets Encantados (+4/+8), Refinados, Oficinas, BUY | `83400, 147940, -3404` |
| **Aden** | Elite High-Five (S80 / S84) | **20** | Sets Vorpal/Elegy/Vesper (+4/+8), Armas Icarus/Vesper/Elegy, Attribute Stones/Crystals, Blessed Enchants S, Top Life Stones | `147450, 25900, -2012` |
| **Oren** | Suprimentos Locais | **5** | Potions, SoE, Shots, Materiais primários | `82698, 53239, -1495` |
| **Hunters** | Suprimentos Locais | **5** | Potions, SoE, Shots, Materiais primários | `116550, 75750, -2700` |
| **Heine** | Suprimentos Locais | **5** | Potions, SoE, Shots, Materiais primários | `111394, 219354, -3544` |
| **Goddard** | Suprimentos Locais | **5** | Potions, SoE, Shots, Materiais primários | `147920, -55300, -2730` |
| **Rune** | Suprimentos Locais | **5** | Potions, SoE, Shots, Materiais primários | `43800, -47700, -790` |
| **Schuttgart** | Suprimentos Locais | **5** | Potions, SoE, Shots, Materiais primários | `87300, -142300, -1340` |

# Modelo de Dados (Data Models)

## Tabela: `fake_players_profiles`
Esta tabela é o coração da persistência do ecossistema de Fake Players.

| Coluna | Tipo SQL | Descrição |
|---|---|---|
| `bot_id` | INT (PK) | Identificador único do bot. |
| `name` | VARCHAR(35) | Nome de exibição do Fake Player. |
| `class_id` | INT | ID da classe (Tanker, DD, Healer, etc). |
| `bot_type` | VARCHAR(20) | Tipo comportamental: `TRADER` ou `HUNTER`. |
| `home_zone` | VARCHAR(50) | Região a qual o bot pertence (ex: `GLUDIO`). |
| `schedule_start` | TIME | Hora em que o bot começa a operar. |
| `schedule_end` | TIME | Hora em que o bot é "desligado" da simulação. |
| `dna_aggressiveness` | TINYINT | Peso de 1 a 10. (Alto = ataca primeiro). |
| `dna_courage` | TINYINT | Peso de 1 a 10. (Baixo = foge com HP baixo). |
| `dna_party_tendency`| TINYINT | Peso de 1 a 10. (Alto = procura grupo). |

## Relacionamentos
- **Inventário**: Para itens, utilizaremos a tabela padrão de `items`, vinculando o `owner_id` ao `bot_id`, para reaproveitar a engine de itens existente do L2Journey.
- **Locais (Location)**: As coordenadas (X, Y, Z, Heading) no momento do último logout serão salvas em uma tabela auxiliar ou reaproveitadas do sistema padrão de `characters`.

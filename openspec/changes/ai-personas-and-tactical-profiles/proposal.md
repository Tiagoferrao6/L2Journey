# Proposal: AI Personas & Tactical Combat Profiles (Crystal, Esquizitinha, Shirou)

## Why

Para evoluir a inteligência dos companions além de papéis genéricos de IA, é necessário desconectar o comportamento cognitivo do personagem de sua classe e implementar **Perfis de Personalidade de IA** com estilos táticos, microgestão avançada, reação a KS/PK e inteligência situacional para PvE e PvP.

## What Changes

- **AI Persona Engine**: Implementar o motor de perfis de personalidade (`AIPersonaProfileManager`) que desacopla o comportamento da IA da classe do bot e carrega personas táticas completas.
- **Perfil 1: Crystal (Silver Ranger - Caçadora Oportunista e Calculista)**:
  - Posicionamento no limite máximo de alcance (*Max Range Attack*).
  - Mecânica de *Kiting* (recuar por 2s ao aproximação de mobs).
  - Priorização de alvos com HP baixo ou *Light Armor*.
  - Reação a KS: Usa *Stunning Shot* no jogador para pará-lo e retoma o mob.
  - Reação a PK/PvP: *Hit & Run* com *Entangle* (slow) e *Scroll of Escape* / *Dash* se HP < 30%.
- **Perfil 2: Esquizitinha (Bishop / Cardinal - Babá Hardcore e Cirúrgica)**:
  - *Dança dos Limiters*: Bloqueia *Balance Life* em Destroyers/Tyrants no momento do *Frenzy* / *Zealot* (HP < 30%) e descarrega *Major Heal* no milissegundo pós-buff.
  - *Gestão de Aggro e MP*: Evita *overheal*; usa *Trance* (Sleep) em mobs desgarados.
  - *Ressurreição Eficiente*: Aguarda controle de área antes do ressurrect de alto nível.
  - *PvP Geometry (LoS Cover)*: Utiliza paredes/pedras para quebrar linha de visão e sai apenas para curar.
  - *Cleanse Prioridade 1*: Remove Stun, Paralysis e Silence antes de curar HP.
  - *Mana Burn Spam* e *Clutch Celestial Shield*: Aplica invulnerabilidade de 10s se um aliado sofrer burst massivo (HP < 10%).
- **Perfil 3: Shirou (Warlord / Paladin - Trator da Linha de Frente)**:
  - *Warlord Training*: Puxa trains de 5 a 10 mobs (Cruma/Catacumbas), usa *Howl* e descarrega *Thunder Storm* / *Earthquake*.
  - *Paladin Frontline*: Usa *Aggression* para puxar aggro e ativa *Angelic Icon* para derreter com velocidade de ataque.
  - *Reação a KS*: Usa *Shock Stomp* (Stun em área) para pausar jogador e mobs simultaneamente.
  - *PvP Defence*: Usa *Rush* para fechar distância; ativa *Ultimate Defense* / *Vengeance* a < 30% HP e cura com *Sacrifice*.

## Capabilities

### New Capabilities

- `ai-persona-profiles`: Gerenciador de perfis de personalidade de IA com traços comportamentais e inteligência situacional.
- `tactical-limiter-healing`: Suporte avançado para gestão de HP baixo e sincronização de *Frenzy/Zealot* para Bishop.
- `kite-and-los-tactics`: Algoritmos de combate espacial (Kiting de Arqueiro e Quebra de Linha de Visão/LoS para Healer).

### Modified Capabilities

- `coop-companion`: Atualizado para vincular instâncias OOG aos perfis de personalidade Crystal, Esquizitinha e Shirou.

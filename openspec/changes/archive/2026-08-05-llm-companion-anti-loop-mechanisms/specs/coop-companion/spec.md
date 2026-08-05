## MODIFIED Requirements

### Requirement: Execução Autônoma de Compras (BuyList Executing Engine)
The system MUST allow AI Companions to target Merchant NPCs (`L2MerchantInstance`), inspect `L2TradeList` items, verify Adena balances, and execute purchase operations (`doBuy`) for required consumables (Soulshots, Health Potions, Scrolls of Escape), enforcing Adena sufficiency checks and cooldown backoff periods to prevent infinite purchasing loops.

#### Scenario: Reabastecimento automático de Soulshots com validação de Adena e Cooldown
- **GIVEN** o inventário do bot possui menos de 100 Soulshots e Adena suficiente (mínimo para compra proporcional)
- **WHEN** o bot atinge a zona da cidade ou o comando de descanso/cidade é ativado
- **THEN** o bot caminha até o NPC Trader de artigos gerais, compra a quantidade adequada de Soulshots via `doBuy`, adiciona ao inventário e ativa a auto-shot; se a Adena for insuficiente, um cooldown é ativado para adiar nova tentativa de compra e o bot é direcionado para caça.

## Why

When a player buys or sells items with a Fake Trader (BUY/SELL private store), the transaction succeeds on the GameServer (Adena and items are transferred in memory), but the player's client inventory UI is not updated. This occurs because `Player._inventoryUpdate` accumulates items without being cleared, sending stale or invalid delta packets, and private store transactions do not force `sendItemList(true)` or `broadcastUserInfo()`.

## What Changes

- Clear `_inventoryUpdate` items in `Player.java` after `sendPacket(_inventoryUpdate)` is executed.
- Call `player.sendItemList(true)` in `TradeList.java` after `privateStoreBuy` and `privateStoreSell` transactions complete so the client UI immediately re-renders inventory items and Adena.
- Update `z_seed_test_account.sql` password insertion to use `ON DUPLICATE KEY UPDATE` to ensure account password updates persist across re-initialization.

## Capabilities

### New Capabilities

### Modified Capabilities
- `fake-traders-engine`: Ensure private store buy/sell transactions properly notify and refresh player inventory UI.

## Impact

- `com.l2journey.gameserver.model.actor.Player`
- `com.l2journey.gameserver.model.TradeList`
- `dist/db_installer/sql/updates/z_seed_test_account.sql`

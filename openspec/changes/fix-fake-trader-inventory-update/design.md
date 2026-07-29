## Context

When players interact with Fake Traders in Lineage II (buying from SELL stores or selling to BUY stores), transactions are executed server-side. However, players reported that items and Adena do not update in their inventory window after purchasing.

Investigation revealed two root causes:
1. `Player._inventoryUpdate` accumulates items indefinitely without clearing its internal entries after `sendPacket(_inventoryUpdate)` fires.
2. `TradeList.privateStoreBuy` and `TradeList.privateStoreSell` only send `sendInventoryUpdate`, but do not trigger `player.sendItemList(true)` to refresh the client UI.

## Goals / Non-Goals

**Goals:**
- Reset `_inventoryUpdate` items in `Player.java` after sending the update packet.
- Trigger `player.sendItemList(true)` in `TradeList.java` after completing private store buy and sell transactions.
- Update `z_seed_test_account.sql` to use `ON DUPLICATE KEY UPDATE` so test account password updates persist across re-initialization.

**Non-Goals:**
- Rewriting the private store UI or client packet protocol.

## Decisions

- **Clear InventoryUpdate on Send**: In `Player.sendInventoryUpdate`, after `sendPacket(_inventoryUpdate)` is invoked, clear the entries to prevent stale accumulative state.
- **Explicit sendItemList after Trade**: Call `player.sendItemList(true)` at the end of `TradeList.privateStoreBuy` and `TradeList.privateStoreSell`.

## Risks / Trade-offs

- **[Risk] Extra Packet Traffic**: `sendItemList(true)` sends full item list.
  - *Mitigation*: Necessary for reliable inventory UI sync upon store transaction completion.

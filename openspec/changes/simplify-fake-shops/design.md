## Context
The server is running a high number of Fake Shops globally, which affects log readability, server testing focus, and pollutes the `characters` table with non-essential entities.

## Goals / Non-Goals
**Goals**:
- Strip down `fake_shops.xml` to contain only the 15 "Lord of the Rings" dwarves in Gludio.
- Distribute their profiles (Buy, Sell, Craft) to comprehensively test `FakeShopData.java` and `city_catalogs.xml`.
- Clean the database table `characters` to remove unused shops.

**Non-Goals**:
- We are not changing the core mechanics of Fake Players.
- We are not changing the prices or contents of `city_catalogs.xml`.

## Decisions
- Keep `<city name="Gludio">` in `fake_shops.xml` and drop all other cities.
- Use the existing `<materials>`, `<supplies>`, and `<items>` from `city_catalogs.xml` for dynamic sellers.
- Use explicit `<customitem>` tags for fixed sellers, buyers, and crafters.
- Execute SQL to delete legacy shops (`DionTrader%`, `GiranSup%`, `AdenElite%`, etc.) from the DB.

## Risks / Trade-offs
- Deleting characters from the DB manually requires care to not delete real test characters. The SQL script will target specific `char_name` patterns previously used for shops.

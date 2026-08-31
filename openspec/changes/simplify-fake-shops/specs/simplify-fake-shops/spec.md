## ADDED Requirements

### Requirement: Isolate Fake Shops to Gludio
The `fake_shops.xml` MUST NOT load any shops outside of Gludio. Only the 15 designated testing dwarves shall exist.

#### Scenario: Server parses fake shops
- **WHEN** the server starts and loads `fake_shops.xml`
- **THEN** exactly 15 fake shops are instantiated, all located in Gludio.

### Requirement: Cover all Fake Shop Profiles
The 15 dwarves MUST cover all combinations of Fake Shop behaviors.

#### Scenario: Test seller profiles
- **GIVEN** Gimli, Thorin, Durin
- **THEN** they must operate as dynamic sellers reading from `city_catalogs.xml`.

#### Scenario: Test fixed sellers and buyers
- **GIVEN** Fixed sellers (Balin, Dwalin, Fili, Kili) and Buyers (Oin, Gloin, Bifur, Bofur)
- **THEN** they must operate using `<customitem>` configurations.

#### Scenario: Test crafters
- **GIVEN** Bombur, Nori, Ori, Dori
- **THEN** they must operate as private manufacture shops.

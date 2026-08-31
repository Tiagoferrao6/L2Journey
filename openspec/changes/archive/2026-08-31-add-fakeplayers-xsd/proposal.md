## Why

The server logs show warnings during XML parsing of `city_catalogs.xml` and `fake_shops.xml`. The XML parser, `IXmlReader`, uses strict validation by default and throws a `cvc-elt.1.a` warning when it cannot find the schema definition (XSD) for the `<catalogs>` and `<shops>` root elements. Providing proper XSD files will eliminate these warnings, ensure that any future modifications to these XML files follow the required structure, and improve data integrity.

## What Changes

- Create `city_catalogs.xsd` defining the schema for `<catalogs>`, `<city>`, `<materials>`, `<supplies>`, `<items>`, and `<item>` nodes.
- Create `fake_shops.xsd` defining the schema for `<shops>`, `<fakeshop>`, and `<customitem>` / `<item>` nodes.
- Update `city_catalogs.xml` to include the `xsi:noNamespaceSchemaLocation` attribute pointing to `city_catalogs.xsd`.
- Update `fake_shops.xml` to include the `xsi:noNamespaceSchemaLocation` attribute pointing to `fake_shops.xsd`.

## Capabilities

### New Capabilities
- `fakeplayers-xsd`: XSD Schemas for Fake Player data files to validate the structure of the fake shop and city catalog XML files.

### Modified Capabilities

## Impact

- `dist/game/data/fakeplayers/city_catalogs.xml`
- `dist/game/data/fakeplayers/fake_shops.xml`
- `dist/game/data/xsd/city_catalogs.xsd` (New file)
- `dist/game/data/xsd/fake_shops.xsd` (New file)
